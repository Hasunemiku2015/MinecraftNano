package com.hasunemiku2015.minecraftnano.builtin

import com.hasunemiku2015.minecraftnano.api.NanoRegistry
import com.hasunemiku2015.minecraftnano.builtin.altfunction.*
import com.hasunemiku2015.minecraftnano.builtin.function.*
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor
import com.hasunemiku2015.minecraftnano.builtin.processor.UndoProcessor

/**
 * Registers all builtin Processors and Functions.
 * @author hasunemiku2015
 * @date 2022/09/26 16:09
 */
object BuiltinRegistry {
    fun registerAll() {
        // Register Postprocessors
        NanoRegistry.registerPostprocessor(DisplayPostProcessor)

        // Register DestructionProcessors
        NanoRegistry.registerDestructionProcessor(DisplayPostProcessor.DisplayDestructionProcessor)
        NanoRegistry.registerDestructionProcessor(ExitChatEvent)
        NanoRegistry.registerDestructionProcessor(GotoLineChatEvent.EventDestructionProcessor)
        NanoRegistry.registerDestructionProcessor(InsertFileChatEvent.EventDestructionProcessor)
        NanoRegistry.registerDestructionProcessor(LineNavigationChatEvent)
        NanoRegistry.registerDestructionProcessor(ReplaceSession.EventDestructionProcessor)
        NanoRegistry.registerDestructionProcessor(ReplaceWithSessionChatEvent.EventLifecycleProcessor)

        // Register LifecycleProcessors
        NanoRegistry.registerLifecycleProcessor(AnchorStore.AnchorProcessor)
        NanoRegistry.registerLifecycleProcessor(SearchConfiguration.SearchProcessor)
        NanoRegistry.registerLifecycleProcessor(SelectionRange.SelectionLifecycleProcessor)
        NanoRegistry.registerLifecycleProcessor(UndoProcessor)

        // Register Functions
        NanoRegistry.registerFunction(BackspaceFunction)
        NanoRegistry.registerFunction(BackwardFunction)
        NanoRegistry.registerFunction(BackwardWordFunction)
        NanoRegistry.registerFunction(CursorPositionFunction)
        NanoRegistry.registerFunction(DeleteFunction)
        NanoRegistry.registerFunction(ExitFunction)
        NanoRegistry.registerFunction(ForwardFunction)
        NanoRegistry.registerFunction(ForwardWordFunction)
        NanoRegistry.registerFunction(GotoLineFunction)
        NanoRegistry.registerFunction(InsertFileFunction)
        NanoRegistry.registerFunction(LineBeginFunction)
        NanoRegistry.registerFunction(LineEndFunction)
        NanoRegistry.registerFunction(NewLineFunction)
        NanoRegistry.registerFunction(NextLineFunction)
        NanoRegistry.registerFunction(NextPageFunction)
        NanoRegistry.registerFunction(PrevLineFunction)
        NanoRegistry.registerFunction(PrevPageFunction)
        NanoRegistry.registerFunction(RefreshFunction)
        NanoRegistry.registerFunction(ReplaceFunction)
        NanoRegistry.registerFunction(SaveFunction)
        NanoRegistry.registerFunction(SearchFunction)
        NanoRegistry.registerFunction(SearchBackwardFunction)
        NanoRegistry.registerFunction(SpaceFunction)
        NanoRegistry.registerFunction(TabFunction)

        // Register AltFunctions
        NanoRegistry.registerAltFunction(AnchorBackwardFunction)
        NanoRegistry.registerAltFunction(AnchorForwardFunction)
        NanoRegistry.registerAltFunction(AnchorModifyFunction)
        NanoRegistry.registerAltFunction(BackspaceWordFunction)
        NanoRegistry.registerAltFunction(CommentFunction)
        NanoRegistry.registerAltFunction(CountWordFunction)
        NanoRegistry.registerAltFunction(DeleteWordFunction)
        NanoRegistry.registerAltFunction(DeleteRegionFunction)
        NanoRegistry.registerAltFunction(FirstLineFunction)
        NanoRegistry.registerAltFunction(GoMatchBracketFunction)
        NanoRegistry.registerAltFunction(IndentFunction)
        NanoRegistry.registerAltFunction(LastLineFunction)
        NanoRegistry.registerAltFunction(NextOccurrenceBackwardFunction)
        NanoRegistry.registerAltFunction(NextOccurrenceForwardFunction)
        NanoRegistry.registerAltFunction(NextTextBlockFunction)
        NanoRegistry.registerAltFunction(PreviousTextBlockFunction)
        NanoRegistry.registerAltFunction(RedoFunction)
        NanoRegistry.registerAltFunction(SelectFunction)
        NanoRegistry.registerAltFunction(UndoFunction)
        NanoRegistry.registerAltFunction(UnIndentFunction)
    }
}