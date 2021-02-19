package net.morher.alarmus.handler;

import net.morher.alarmus.api.ServerMessage;

public class TaskCompleteHandler implements ClientMessageHandler {

    private static final String TASK_COMPLETE_HOOK_PREFIX = "taskComplete:";

    @Override
    public void handleClientEvent(ServerMessage event, CoordinatorContext ctx) {
        if (event.getHook() != null
                && event.getHook().startsWith(TASK_COMPLETE_HOOK_PREFIX)) {
            String taskToken = event.getHook().substring(TASK_COMPLETE_HOOK_PREFIX.length());
            ctx.reportTaskComplete(taskToken);
        }
        if (event.getCompletedTaskToken() != null) {
            ctx.reportTaskComplete(event.getCompletedTaskToken());
        }
    }

}
