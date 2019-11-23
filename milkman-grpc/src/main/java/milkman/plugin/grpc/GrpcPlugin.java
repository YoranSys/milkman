package milkman.plugin.grpc;

import java.util.List;

import milkman.domain.RequestContainer;
import milkman.domain.RequestExecutionContext;
import milkman.domain.ResponseContainer;
import milkman.plugin.grpc.domain.GrpcOperationAspect;
import milkman.plugin.grpc.domain.GrpcPayloadAspect;
import milkman.plugin.grpc.domain.GrpcRequestContainer;
import milkman.plugin.grpc.editor.GrpcOperationAspectEditor;
import milkman.plugin.grpc.editor.GrpcPayloadAspectEditor;
import milkman.plugin.grpc.editor.GrpcRequestEditor;
import milkman.plugin.grpc.editor.GrpcResponsePayloadEditor;
import milkman.plugin.grpc.processor.GrpcMetaProcessor;
import milkman.plugin.grpc.processor.GrpcRequestProcessor;
import milkman.ui.plugin.CustomCommand;
import milkman.ui.plugin.RequestAspectEditor;
import milkman.ui.plugin.RequestAspectsPlugin;
import milkman.ui.plugin.RequestTypeEditor;
import milkman.ui.plugin.RequestTypePlugin;
import milkman.ui.plugin.ResponseAspectEditor;
import milkman.ui.plugin.Templater;

public class GrpcPlugin implements RequestTypePlugin, RequestAspectsPlugin {

	private GrpcRequestProcessor processor = new GrpcRequestProcessor();
	private GrpcMetaProcessor metaProcessor = new GrpcMetaProcessor();

	@Override
	public List<RequestAspectEditor> getRequestTabs() {
		return List.of(new GrpcOperationAspectEditor(), new GrpcPayloadAspectEditor());
	}

	@Override
	public List<ResponseAspectEditor> getResponseTabs() {
		return List.of(new GrpcResponsePayloadEditor());
	}

	@Override
	public void initializeRequestAspects(RequestContainer request) {
		if (request instanceof GrpcRequestContainer) {
			if (request.getAspect(GrpcOperationAspect.class).isEmpty()) {
				request.addAspect(new GrpcOperationAspect());
			}
			if (request.getAspect(GrpcPayloadAspect.class).isEmpty()) {
				request.addAspect(new GrpcPayloadAspect());
			}
		}
	}

	@Override
	public void initializeResponseAspects(RequestContainer request, ResponseContainer response,
			RequestExecutionContext context) {
		// nothing to do, we created the request
	}

	@Override
	public RequestContainer createNewRequest() {
		return new GrpcRequestContainer("New Grpc Request", "");
	}

	@Override
	public RequestTypeEditor getRequestEditor() {
		return new GrpcRequestEditor();
	}

	@Override
	public ResponseContainer executeRequest(RequestContainer request, Templater templater) {
		if (!(request instanceof GrpcRequestContainer)) {
			throw new IllegalArgumentException("Unsupported request type");
		}

		return processor.executeRequest((GrpcRequestContainer) request, templater);
	}

	@Override
	public List<CustomCommand> getCustomCommands() {
		return List.of(new CustomCommand("LIST_SERVICES", "List Services"));
	}

	@Override
	public ResponseContainer executeCustomCommand(String commandId, RequestContainer request, Templater templater) {
		if (!(request instanceof GrpcRequestContainer)) {
			throw new IllegalArgumentException("Unsupported request type");
		}

		switch (commandId) {
		case "LIST_SERVICES":
			return metaProcessor.listServices((GrpcRequestContainer) request, templater);
		default:
			throw new IllegalArgumentException("Unsupported custom command: " + commandId);
		}
	}

	@Override
	public String getRequestType() {
		return "Grpc";
	}

	@Override
	public boolean canHandle(RequestContainer request) {
		return request instanceof GrpcRequestContainer;
	}

	@Override
	public int getOrder() {
		return 18;
	}

}
