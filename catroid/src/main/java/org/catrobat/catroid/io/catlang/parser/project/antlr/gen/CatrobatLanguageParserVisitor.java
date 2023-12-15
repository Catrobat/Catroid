// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/project/antlr/CatrobatLanguageParser.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.project.antlr.gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CatrobatLanguageParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CatrobatLanguageParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(CatrobatLanguageParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#programHeader}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgramHeader(CatrobatLanguageParser.ProgramHeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#programBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgramBody(CatrobatLanguageParser.ProgramBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#metadata}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetadata(CatrobatLanguageParser.MetadataContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#metadataContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetadataContent(CatrobatLanguageParser.MetadataContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDescription(CatrobatLanguageParser.DescriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#catrobatVersion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCatrobatVersion(CatrobatLanguageParser.CatrobatVersionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#catrobatAppVersion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCatrobatAppVersion(CatrobatLanguageParser.CatrobatAppVersionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#stage}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStage(CatrobatLanguageParser.StageContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#stageContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStageContent(CatrobatLanguageParser.StageContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#landscapeMode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLandscapeMode(CatrobatLanguageParser.LandscapeModeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#height}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeight(CatrobatLanguageParser.HeightContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#width}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidth(CatrobatLanguageParser.WidthContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#displayMode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDisplayMode(CatrobatLanguageParser.DisplayModeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#globals}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobals(CatrobatLanguageParser.GlobalsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#multiplayerVariables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplayerVariables(CatrobatLanguageParser.MultiplayerVariablesContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#variableOrListDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableOrListDeclaration(CatrobatLanguageParser.VariableOrListDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(CatrobatLanguageParser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#scene}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScene(CatrobatLanguageParser.SceneContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#background}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBackground(CatrobatLanguageParser.BackgroundContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#actor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActor(CatrobatLanguageParser.ActorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#actorContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActorContent(CatrobatLanguageParser.ActorContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#localVariables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalVariables(CatrobatLanguageParser.LocalVariablesContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#looks}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLooks(CatrobatLanguageParser.LooksContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#sounds}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSounds(CatrobatLanguageParser.SoundsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#looksAndSoundsContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLooksAndSoundsContent(CatrobatLanguageParser.LooksAndSoundsContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#scripts}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScripts(CatrobatLanguageParser.ScriptsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#userDefinedScripts}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUserDefinedScripts(CatrobatLanguageParser.UserDefinedScriptsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#userDefinedScript}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUserDefinedScript(CatrobatLanguageParser.UserDefinedScriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#brick_defintion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBrick_defintion(CatrobatLanguageParser.Brick_defintionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#userDefinedBrick}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUserDefinedBrick(CatrobatLanguageParser.UserDefinedBrickContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#userDefinedBrickPart}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUserDefinedBrickPart(CatrobatLanguageParser.UserDefinedBrickPartContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#brick_with_body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBrick_with_body(CatrobatLanguageParser.Brick_with_bodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#brick_invocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBrick_invocation(CatrobatLanguageParser.Brick_invocationContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#brick_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBrick_condition(CatrobatLanguageParser.Brick_conditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#arg_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg_list(CatrobatLanguageParser.Arg_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument(CatrobatLanguageParser.ArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatLanguageParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormula(CatrobatLanguageParser.FormulaContext ctx);
}