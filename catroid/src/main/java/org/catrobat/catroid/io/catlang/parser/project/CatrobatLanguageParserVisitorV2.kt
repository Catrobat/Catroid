/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.io.catlang.parser.project

import android.content.Context
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.CompositeBrick
import org.catrobat.catroid.content.bricks.NoteBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParserVisitor
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageArgumentResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageBaseResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageBrickListResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageBrickResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageIntervalResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageKeyValueResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageListResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageProgramVisitResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageStringResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageUserDefinedBrickInputResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageUserDefinedBrickLabelResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageUserDefinedBrickResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageUserDefinedScriptResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageVariableResult
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException
import java.util.Stack
import kotlin.math.max
import kotlin.math.min

class CatrobatLanguageParserVisitorV2(private val context: Context) : CatrobatLanguageParserVisitor<CatrobatLanguageBaseResult> {
    private val project = Project()
    private var currentScene: Scene? = null
    private var currentSprite: Sprite? = null

    private val userDefinedBricks = mutableMapOf<String, UserDefinedBrick>()

    private val parentBrickStack = Stack<Brick>()

    override fun visit(tree: ParseTree?): CatrobatLanguageBaseResult {
        throw NotImplementedError("Not needed.")
    }

    override fun visitChildren(node: RuleNode?): CatrobatLanguageBaseResult {
        throw NotImplementedError("Not needed.")
    }

    override fun visitTerminal(node: TerminalNode?): CatrobatLanguageBaseResult {
        throw NotImplementedError("Not needed.")
    }

    override fun visitErrorNode(node: ErrorNode?): CatrobatLanguageBaseResult {
        throw NotImplementedError("Not needed.")
    }

    override fun visitProgram(ctx: CatrobatLanguageParser.ProgramContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid program definition found.")
        }
        visitProgramHeader(ctx.programHeader())
        visitProgramBody(ctx.programBody())

        return CatrobatLanguageProgramVisitResult(project)
    }

    override fun visitProgramHeader(ctx: CatrobatLanguageParser.ProgramHeaderContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid program header found.")
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitProgramBody(ctx: CatrobatLanguageParser.ProgramBodyContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid program body found.")
        }
        this.project.name = CatrobatLanguageParserHelper.getStringContent(ctx.STRING().text)

        if (ctx.metadata() == null || ctx.metadata().size != 1) {
            throw CatrobatLanguageParsingException("Metadata must occur exactly once")
        }

        if (ctx.stage() == null || ctx.stage().size != 1) {
            throw CatrobatLanguageParsingException("Stage must occur exactly once")
        }

        if (ctx.globals() == null || ctx.globals().size > 1) {
            throw CatrobatLanguageParsingException("Globals must occur exactly once")
        }

        if (ctx.multiplayerVariables() != null && ctx.multiplayerVariables().size > 1) {
            throw CatrobatLanguageParsingException("Multiplayer variables must occur at most once")
        } else if (ctx.multiplayerVariables() != null && ctx.multiplayerVariables().size == 1) {
            visitMultiplayerVariables(ctx.multiplayerVariables()[0])
        }

        visitMetadata(ctx.metadata()[0])
        visitStage(ctx.stage()[0])
        if (ctx.globals() != null && ctx.globals().size == 1) {
            visitGlobals(ctx.globals()[0])
        }

        ctx.scene().forEach {
            visitScene(it)
        }

        return CatrobatLanguageBaseResult()
    }

    private val loadedMetadata = setOf<String>()
    override fun visitMetadata(ctx: CatrobatLanguageParser.MetadataContext?): CatrobatLanguageBaseResult {
        if (ctx?.metadataContent() == null) {
            throw CatrobatLanguageParsingException("No valid metadata found.")
        }
        ctx.metadataContent().forEach {
            visitMetadataContent(it)
        }
//        if (loadedMetadata.size != 3) {
//            throw CatrobatLanguageParsingException("The following 3 metadata must occur exactly once each: Description, Catrobat version, Catrobat app version")
//        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitMetadataContent(ctx: CatrobatLanguageParser.MetadataContentContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid metadata content found.")
        }
        if (ctx.description() != null) {
            visitDescription(ctx.description())
        } else if (ctx.catrobatVersion() != null) {
            visitCatrobatVersion(ctx.catrobatVersion())
        } else if (ctx.catrobatAppVersion() != null) {
            visitCatrobatAppVersion(ctx.catrobatAppVersion())
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitDescription(ctx: CatrobatLanguageParser.DescriptionContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid description found.")
        }
        if (loadedMetadata.contains(ctx.DESCRIPTION().text)) {
            throw CatrobatLanguageParsingException("Description must occur exactly once")
        }
        loadedMetadata.plus(ctx.DESCRIPTION().text)
        project.description = CatrobatLanguageParserHelper.getStringContent(ctx.STRING().text)
        return CatrobatLanguageBaseResult()
    }

    override fun visitCatrobatVersion(ctx: CatrobatLanguageParser.CatrobatVersionContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid Catrobat version found.")
        }
        if (loadedMetadata.contains(ctx.CATROBAT_VERSION().text)) {
            throw CatrobatLanguageParsingException("Catrobat version must occur exactly once")
        }
        loadedMetadata.plus(ctx.CATROBAT_VERSION().text)

        try {
            project.catrobatLanguageVersion = CatrobatLanguageParserHelper.getStringToDouble(ctx.STRING().text)
        } catch (exception: NumberFormatException) {
            throw CatrobatLanguageParsingException("Catrobat version must be a valid numerical value")
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitCatrobatAppVersion(ctx: CatrobatLanguageParser.CatrobatAppVersionContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid Catrobat app version found.")
        }
        if (loadedMetadata.contains(ctx.CATRPBAT_APP_VERSION().text)) {
            throw CatrobatLanguageParsingException("Catrobat app version must occur exactly once")
        }
        loadedMetadata.plus(ctx.CATRPBAT_APP_VERSION().text)
        project.applicationVersion = CatrobatLanguageParserHelper.getStringContent(ctx.STRING().text)
        return CatrobatLanguageBaseResult()
    }

    private val loadedStage = setOf<String>()
    override fun visitStage(ctx: CatrobatLanguageParser.StageContext?): CatrobatLanguageBaseResult {
        if (ctx?.stageContent() == null) {
            throw CatrobatLanguageParsingException("No valid stage found.")
        }

        ctx.stageContent().forEach {
            visitStageContent(it)
        }

//        if (loadedStage.size != 4) {
//            throw CatrobatLanguageParsingException("The following 4 stage content must occur exactly once each: Landscape mode, Display mode, Height, Width")
//        }

        return CatrobatLanguageBaseResult()
    }

    override fun visitStageContent(ctx: CatrobatLanguageParser.StageContentContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid stage content found.")
        }
        if (ctx.landscapeMode() != null) {
            visitLandscapeMode(ctx.landscapeMode())
        } else if (ctx.displayMode() != null) {
            visitDisplayMode(ctx.displayMode())
        } else if (ctx.height() != null) {
            visitHeight(ctx.height())
        } else if (ctx.width() != null) {
            visitWidth(ctx.width())
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitLandscapeMode(ctx: CatrobatLanguageParser.LandscapeModeContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid landscape mode found.")
        }
        if (loadedStage.contains(ctx.LANDSCAPE_MODE().text)) {
            throw CatrobatLanguageParsingException("Landscape mode must occur exactly once")
        }
        loadedStage.plus(ctx.LANDSCAPE_MODE().text)
        project.setlandscapeMode(CatrobatLanguageParserHelper.getStringToBoolean(ctx.STRING().text))
        return CatrobatLanguageBaseResult()
    }

    override fun visitHeight(ctx: CatrobatLanguageParser.HeightContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid height found.")
        }
        if (loadedStage.contains(ctx.HEIGHT().text)) {
            throw CatrobatLanguageParsingException("Height must occur exactly once")
        }
        loadedStage.plus(ctx.HEIGHT().text)
        try {
            project.setScreenHeight(CatrobatLanguageParserHelper.getStringToInt(ctx.STRING().text))
        } catch (exception: NumberFormatException) {
            throw CatrobatLanguageParsingException("Height must be a valid numerical value")
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitWidth(ctx: CatrobatLanguageParser.WidthContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid width found.")
        }
        if (loadedStage.contains(ctx.WIDTH().text)) {
            throw CatrobatLanguageParsingException("Width must occur exactly once")
        }
        loadedStage.plus(ctx.WIDTH().text)
        try {
            project.setScreenWidth(CatrobatLanguageParserHelper.getStringToInt(ctx.STRING().text))
        } catch (exception: NumberFormatException) {
            throw CatrobatLanguageParsingException("Width must be a valid numerical value")
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitDisplayMode(ctx: CatrobatLanguageParser.DisplayModeContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid display mode found.")
        }
        if (loadedStage.contains(ctx.DISPLAY_MODE().text)) {
            throw CatrobatLanguageParsingException("Display mode must occur exactly once")
        }
        loadedStage.plus(ctx.DISPLAY_MODE().text)

        ScreenModes.values().forEach {
            if (it.name.equals(CatrobatLanguageParserHelper.getStringContent(ctx.STRING().text), ignoreCase = true)) {
                project.screenMode = it
                return CatrobatLanguageBaseResult()
            }
        }

        throw CatrobatLanguageParsingException("Display mode must be one of the following: ${ScreenModes.values().joinToString(", ")}")
    }

    override fun visitGlobals(ctx: CatrobatLanguageParser.GlobalsContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid globals found.")
        }
        if (ctx.variableOrListDeclaration() == null || ctx.variableOrListDeclaration().size == 0) {
            throw CatrobatLanguageParsingException("No valid variable or list declaration found.")
        }
        ctx.variableOrListDeclaration().forEach {
            visitVariableOrListDeclaration(it)
        }

        ctx.variableOrListDeclaration().forEach {
            val result = visitVariableOrListDeclaration(it)
            if (result is CatrobatLanguageListResult) {
                project.userLists.add(UserList(result.listName))
            } else if (result is CatrobatLanguageVariableResult) {
                project.userVariables.add(UserVariable(result.variableName))
            } else {
                throw CatrobatLanguageParsingException("No valid variable or list declaration found.")
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitMultiplayerVariables(ctx: CatrobatLanguageParser.MultiplayerVariablesContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid multiplayer variables found.")
        }
        ctx.variableDeclaration().forEach {
            val variableName = (visitVariableDeclaration(it) as CatrobatLanguageVariableResult).variableName
            project.multiplayerVariables.add(UserVariable(variableName))
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitVariableOrListDeclaration(ctx: CatrobatLanguageParser.VariableOrListDeclarationContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid variable or list declaration found.")
        }
        if (ctx.variableDeclaration() != null) {
            return visitVariableDeclaration(ctx.variableDeclaration())
        }
        return CatrobatLanguageListResult(CatrobatLanguageParserHelper.getListName(ctx.LIST_REF().text))
    }

    override fun visitVariableDeclaration(ctx: CatrobatLanguageParser.VariableDeclarationContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid variable declaration found.")
        }
        return CatrobatLanguageVariableResult(CatrobatLanguageParserHelper.getVariableName(ctx.VARIABLE_REF().text));
    }

    override fun visitScene(ctx: CatrobatLanguageParser.SceneContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid scene found.")
        }
        val sceneName = CatrobatLanguageParserHelper.getStringContent(ctx.STRING().text)
        val scene = Scene(sceneName, project)
        currentScene = scene
        project.sceneList.add(scene)

        visitBackground(ctx.background())

        ctx.actor().forEach {
            visitActor(it)
        }

        currentScene = null
        return CatrobatLanguageBaseResult()
    }

    override fun visitBackground(ctx: CatrobatLanguageParser.BackgroundContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid background found.")
        }
        val backgroundSprite = Sprite("Background")
        currentScene!!.spriteList.add(0, backgroundSprite)
        currentSprite = backgroundSprite

        userDefinedBricks.clear()
        userDefinedBricksInitialized = false
        visitActorContent(ctx.actorContent())

        currentSprite = null
        return CatrobatLanguageBaseResult()
    }

    override fun visitActor(ctx: CatrobatLanguageParser.ActorContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid actor found.")
        }
        val sprite = Sprite(CatrobatLanguageParserHelper.getStringContent(ctx.STRING(0).text))
        currentSprite = sprite
        currentScene!!.spriteList.add(sprite)

        userDefinedBricks.clear()
        userDefinedBricksInitialized = false
        visitActorContent(ctx.actorContent())

        currentSprite = null
        return CatrobatLanguageBaseResult()
    }

    override fun visitActorContent(ctx: CatrobatLanguageParser.ActorContentContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid actor content found.")
        }

        if (ctx.localVariables() != null && ctx.localVariables().count() > 1) {
            throw CatrobatLanguageParsingException("Local variables must occur at most once in each actor/object.")
        }
        if (ctx.looks() != null && ctx.looks().count() > 1) {
            throw CatrobatLanguageParsingException("Looks must occur at most once in each actor/object.")
        }
        if (ctx.sounds() != null && ctx.sounds().count() > 1) {
            throw CatrobatLanguageParsingException("Sounds must occur at most once in each actor/object.")
        }
        if (ctx.userDefinedScripts() != null && ctx.userDefinedScripts().count() > 1) {
            throw CatrobatLanguageParsingException("User defined scripts must occur at most once in each actor/object.")
        }
        if (ctx.userDefinedScripts() != null && ctx.userDefinedScripts().count() > 1) {
            throw CatrobatLanguageParsingException("User defined scripts must occur at most once in each actor/object.")
        }

        if (ctx.localVariables() != null && ctx.localVariables().count() == 1) {
            visitLocalVariables(ctx.localVariables()[0])
        }
        if (ctx.looks() != null && ctx.looks().count() == 1) {
            visitLooks(ctx.looks()[0])
        }
        if (ctx.sounds() != null && ctx.sounds().count() == 1) {
            visitSounds(ctx.sounds()[0])
        }
        if (ctx.userDefinedScripts() != null && ctx.userDefinedScripts().count() == 1) {
            visitUserDefinedScripts(ctx.userDefinedScripts()[0])
        }

        if (ctx.scripts() != null) {
            ctx.scripts().forEach {
                visitScripts(it)
            }
        }

        return CatrobatLanguageBaseResult()
    }

    override fun visitLocalVariables(ctx: CatrobatLanguageParser.LocalVariablesContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid local variables found.")
        }
        ctx.variableOrListDeclaration().forEach {
            val result = visitVariableOrListDeclaration(it)
            if (result is CatrobatLanguageListResult) {
                currentSprite!!.userLists.add(UserList(result.listName))
            } else if (result is CatrobatLanguageVariableResult) {
                currentSprite!!.userVariables.add(UserVariable(result.variableName))
            } else {
                throw CatrobatLanguageParsingException("No valid variable or list declaration found.")
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitLooks(ctx: CatrobatLanguageParser.LooksContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitSounds(ctx: CatrobatLanguageParser.SoundsContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitLooksAndSoundsContent(ctx: CatrobatLanguageParser.LooksAndSoundsContentContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid file definition content found.")
        }
        if (ctx.STRING().size != 2) {
            throw CatrobatLanguageParsingException("The file definition must contain the name and the file identifier.")
        }
        val lookOrSoundName = CatrobatLanguageParserHelper.getStringContent(ctx.STRING(0).text)
        val lookOrSoundFileName = CatrobatLanguageParserHelper.getStringContent(ctx.STRING(1).text)
        return CatrobatLanguageKeyValueResult(lookOrSoundName, lookOrSoundFileName)
    }

    override fun visitScripts(ctx: CatrobatLanguageParser.ScriptsContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid scripts found.")
        }
        ctx.brick_with_body().forEach {
            val scriptBrick = (visitBrick_with_body(it) as CatrobatLanguageBrickResult).brick as ScriptBrick
            currentSprite!!.scriptList.add(scriptBrick.script)
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitBrick_defintion(ctx: CatrobatLanguageParser.Brick_defintionContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid brick definition found.")
        }

        if (ctx.brick_invocation() != null) {
            return visitBrick_invocation(ctx.brick_invocation())
        }
        if (ctx.brick_with_body() != null) {
            return visitBrick_with_body(ctx.brick_with_body())
        }
        if (ctx.NOTE_BRICK() != null) {
            return CatrobatLanguageBrickResult(NoteBrick(ctx.NOTE_BRICK().text), mapOf())
        }

        return CatrobatLanguageBaseResult()
    }

    override fun visitBrick_with_body(ctx: CatrobatLanguageParser.Brick_with_bodyContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid brick found.")
        }

        val arguments: Map<String, String> = if (ctx.brick_condition() != null) {
            val result = visitBrick_condition(ctx.brick_condition()) as CatrobatLanguageArgumentResult
            result.arguments
        } else {
            mapOf()
        }

        val brick: Brick;
        if (ctx.parent is CatrobatLanguageParser.ScriptsContext) {
            if (ctx.elseBranch() != null) {
                throw CatrobatLanguageParsingException("Else branch must not occur in scripts.")
            }
            brick = ScriptFactory.createScriptFromCatrobatLanguage(ctx.BRICK_NAME().text, arguments.keys.toList()).scriptBrick
            parentBrickStack.push(brick)
            brick.setParameters(context, project, currentScene!!, currentSprite!!, arguments)
        } else if (ctx.parent is CatrobatLanguageParser.Brick_defintionContext) {
            val elseBranchPresent = ctx.elseBranch() != null
            brick = BrickFactory.createBrickFromCatrobatLanguage(ctx.BRICK_NAME().text, arguments, elseBranchPresent)
            brick.parent = if (parentBrickStack.isEmpty()) {
                null
            } else {
                parentBrickStack.peek()
            }
            parentBrickStack.push(brick)
            if (brick !is CompositeBrick) {
                throw CatrobatLanguageParsingException("Brick ${ctx.BRICK_NAME().text} must not have a body.")
            }
            if (!brick.hasSecondaryList() && elseBranchPresent) {
                throw CatrobatLanguageParsingException("Brick ${ctx.BRICK_NAME().text} must not have an else branch.")
            }
            if (brick.hasSecondaryList() && !elseBranchPresent) {
                throw CatrobatLanguageParsingException("Brick ${ctx.BRICK_NAME().text} must have an else branch.")
            }

            brick.setParameters(context, project, currentScene!!, currentSprite!!, arguments)
        } else {
            throw IllegalArgumentException("Unknown context parent: ${ctx.parent}")
        }

        if (ctx.brick_defintion() != null) {
            ctx.brick_defintion().forEach {
                val subBrick = (visitBrick_defintion(it) as CatrobatLanguageBrickResult).brick
                if (brick is ScriptBrick) {
                    brick.script.brickList.add(subBrick)
                } else if (brick is CompositeBrick) {
                    brick.nestedBricks.add(subBrick)
                }
            }
        }

        if (ctx.elseBranch() != null && brick is CompositeBrick && brick.hasSecondaryList()) {
            parentBrickStack.push(brick.secondaryNestedBricksParent)
            val elseBranch = (visitElseBranch(ctx.elseBranch()) as CatrobatLanguageBrickListResult).bricks
            parentBrickStack.pop()
            brick.secondaryNestedBricks.addAll(elseBranch)
        }

        parentBrickStack.pop()

        return CatrobatLanguageBrickResult(brick, arguments)
    }

    override fun visitElseBranch(ctx: CatrobatLanguageParser.ElseBranchContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid else branch found.")
        }
        val bricks = arrayListOf<Brick>()
        if (ctx.brick_defintion() != null) {
            ctx.brick_defintion().forEach {
                val brick = (visitBrick_defintion(it) as CatrobatLanguageBrickResult).brick
                bricks.add(brick)
            }
        }
        return CatrobatLanguageBrickListResult(bricks)
    }

    override fun visitBrick_invocation(ctx: CatrobatLanguageParser.Brick_invocationContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid brick invocation found.")
        }

        val arguments: Map<String, String> = if (ctx.brick_condition() != null) {
            val result = visitBrick_condition(ctx.brick_condition()) as CatrobatLanguageArgumentResult
            result.arguments
        } else {
            mapOf()
        }

        if (ctx.BRICK_NAME() != null) {
            val brick = BrickFactory.createBrickFromCatrobatLanguage(ctx.BRICK_NAME().text, arguments, false)
            brick.parent = if (parentBrickStack.isEmpty()) {
                null
            } else {
                parentBrickStack.peek()
            }
            brick.setParameters(context, project, currentScene!!, currentSprite!!, arguments)
            return CatrobatLanguageBrickResult(brick, arguments)
        }
        if (ctx.userDefinedBrick() != null) {
            val userDefinedBrickResult = visitUserDefinedBrick(ctx.userDefinedBrick()) as CatrobatLanguageUserDefinedBrickResult
            if (!userDefinedBricks.containsKey(userDefinedBrickResult.userDefinedBrickText)) {
                throw CatrobatLanguageParsingException("User defined brick ${userDefinedBrickResult.userDefinedBrickText} must be defined before it can be used.")
            }
            val userDefinedBrick = userDefinedBricks[userDefinedBrickResult.userDefinedBrickText]
            val userDefinedBrickCopy = UserDefinedBrick(userDefinedBrick!!)
            userDefinedBrickCopy.setCallingBrick(true)
            return CatrobatLanguageBrickResult(userDefinedBrickCopy, arguments)
        }
        throw IllegalArgumentException("Unknown brick invocation: ${ctx.text}")
    }

    override fun visitBrick_condition(ctx: CatrobatLanguageParser.Brick_conditionContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid brick condition found.")
        }

        if (ctx.arg_list() != null) {
            return visitArg_list(ctx.arg_list());
        }
        return CatrobatLanguageArgumentResult(mapOf())
    }

    override fun visitArg_list(ctx: CatrobatLanguageParser.Arg_listContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid argument list found.")
        }
        val arguments = mutableMapOf<String, String>()
        ctx.argument().forEach {
            val argumentResult = visitArgument(it) as CatrobatLanguageKeyValueResult
            if (arguments.containsKey(argumentResult.key)) {
                throw CatrobatLanguageParsingException("Argument ${argumentResult.key} must not occur more than once.")
            }
            arguments[argumentResult.key] = argumentResult.value
        }
        return CatrobatLanguageArgumentResult(arguments)
    }

    override fun visitArgument(ctx: CatrobatLanguageParser.ArgumentContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid argument found.")
        }
        val argumentName = ctx.PARAM_MODE_NAME().text.trim()
        val formulaString = (visitFormula(ctx.formula()) as CatrobatLanguageStringResult).string
        return CatrobatLanguageKeyValueResult(argumentName, formulaString)
    }

    override fun visitFormula(ctx: CatrobatLanguageParser.FormulaContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid formula found.")
        }
        val formulaString = StringBuilder()
        ctx.formulaElement().forEach {
            val formulaElementResult = visitFormulaElement(it) as CatrobatLanguageStringResult
            formulaString.append(formulaElementResult.string)
        }
        return CatrobatLanguageStringResult(formulaString.toString())
    }

    override fun visitFormulaElement(ctx: CatrobatLanguageParser.FormulaElementContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid formula element found.")
        }

        if (ctx.FORMULA_MODE_BRACKET_OPEN() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_MODE_BRACKET_OPEN().text)
        }
        if (ctx.FORMULA_MODE_BRACKET_CLOSE() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_MODE_BRACKET_CLOSE().text)
        }
        if (ctx.FORMULA_MODE_ANYTHING() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_MODE_ANYTHING().text)
        }

        if (ctx.FORMULA_MODE_STRING_BEGIN() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_MODE_STRING_BEGIN().text)
        }
        if (ctx.FORMULA_STRING_MODE_END() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_STRING_MODE_END().text)
        }
        if (ctx.FORMULA_STRING_MODE_ANYTHING() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_STRING_MODE_ANYTHING().text)
        }

        if (ctx.FORMULA_MODE_VARIABLE_BEGIN() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_MODE_VARIABLE_BEGIN().text)
        }
        if (ctx.FORMULA_VARIABLE_MODE_END() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_VARIABLE_MODE_END().text)
        }
        if (ctx.FORMULA_VARIABLE_MODE_ANYTHING() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_VARIABLE_MODE_ANYTHING().text)
        }

        if (ctx.FORMULA_MODE_UDB_PARAM_BEGIN() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_MODE_UDB_PARAM_BEGIN().text)
        }
        if (ctx.FORMULA_UDB_PARAM_MODE_END() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_UDB_PARAM_MODE_END().text)
        }
        if (ctx.FORMULA_UDB_PARAM_MODE_ANYTHING() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_UDB_PARAM_MODE_ANYTHING().text)
        }

        if (ctx.FORMULA_LIST_MODE_BEGIN() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_LIST_MODE_BEGIN().text)
        }
        if (ctx.FORMULA_LIST_MODE_END() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_LIST_MODE_END().text)
        }
        if (ctx.FORMULA_LIST_MODE_ANYTHING() != null) {
            return CatrobatLanguageStringResult(ctx.FORMULA_LIST_MODE_ANYTHING().text)
        }

        throw IllegalArgumentException("Unknown formula element.")
    }

    private var userDefinedBricksInitialized: Boolean = false

    override fun visitUserDefinedScripts(ctx: CatrobatLanguageParser.UserDefinedScriptsContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid user defined scripts found.")
        }

        ctx.userDefinedScript().forEach {
            val userDefinedBrickResult = visitUserDefinedScript(it) as CatrobatLanguageUserDefinedBrickResult
            userDefinedBricks[userDefinedBrickResult.userDefinedBrickText] = userDefinedBrickResult.userDefinedBrick
            currentSprite!!.userDefinedBrickList.add(userDefinedBrickResult.userDefinedBrick)
        }

        userDefinedBricksInitialized = true

        ctx.userDefinedScript().forEach {
            val userDefinedScriptResult = visitUserDefinedScript(it) as CatrobatLanguageUserDefinedScriptResult
            currentSprite!!.scriptList.add(userDefinedScriptResult.userDefinedScript.script)
        }

        return CatrobatLanguageBaseResult()
    }

    override fun visitUserDefinedScript(ctx: CatrobatLanguageParser.UserDefinedScriptContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid user defined script found.")
        }

        val userDefinedBrickResult = visitUserDefinedBrick(ctx.userDefinedBrick()) as CatrobatLanguageUserDefinedBrickResult
        if (!userDefinedBricksInitialized) {
            userDefinedBrickResult.userDefinedBrick.setCallingBrick(false)
            return userDefinedBrickResult
        }

        val userDefinedBrick = userDefinedBricks[userDefinedBrickResult.userDefinedBrickText]
        val userDefinedScript = UserDefinedReceiverBrick(userDefinedBrick)

        if (ctx.brick_defintion() != null) {
            ctx.brick_defintion().forEach {
                val subBrick = (visitBrick_defintion(it) as CatrobatLanguageBrickResult).brick
                subBrick.parent = userDefinedScript
                userDefinedScript.script.brickList.add(subBrick)
            }
        }
        return CatrobatLanguageUserDefinedScriptResult(userDefinedScript)
    }

    override fun visitUserDefinedBrick(ctx: CatrobatLanguageParser.UserDefinedBrickContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid user defined brick found.")
        }
        val udb = UserDefinedBrick()
        val udbString = StringBuilder()

        ctx.userDefinedBrickPart().forEach {
            val userDefinedBrickPartResult = visitUserDefinedBrickPart(it)
            if (userDefinedBrickPartResult is CatrobatLanguageUserDefinedBrickLabelResult) {
                udb.addLabel(userDefinedBrickPartResult.label)
                udbString.append(userDefinedBrickPartResult.label)
            }
            if (userDefinedBrickPartResult is CatrobatLanguageUserDefinedBrickInputResult) {
                udb.addInput(userDefinedBrickPartResult.input)
                udbString.append("[${userDefinedBrickPartResult.input}]")
            }
        }
        return CatrobatLanguageUserDefinedBrickResult(udbString.toString(), udb)
    }

    override fun visitUserDefinedBrickPart(ctx: CatrobatLanguageParser.UserDefinedBrickPartContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid user defined brick part found.")
        }

        if (ctx.UDB_LABEL() != null) {
            return CatrobatLanguageUserDefinedBrickLabelResult(ctx.UDB_LABEL().text.trim())
        }
        if (ctx.UDB_PARAM_TEXT() != null) {
            return CatrobatLanguageUserDefinedBrickInputResult(ctx.UDB_PARAM_TEXT().text.trim())
        }

        throw IllegalArgumentException("Undefined user defined brick part - all null")
    }
}