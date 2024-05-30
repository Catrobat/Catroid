import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.R
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity
import org.catrobat.catroid.ui.recyclerview.controller.LookController
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.java.KoinJavaComponent

@RunWith(Parameterized::class)
class BackpackUndoTest(private val fragmentId: Int) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(BackpackActivity.FRAGMENT_SPRITES),
                //arrayOf(BackpackActivity.FRAGMENT_SCENES),
                //arrayOf(BackpackActivity.FRAGMENT_SCRIPTS),
                arrayOf(BackpackActivity.FRAGMENT_LOOKS)
                //arrayOf(BackpackActivity.FRAGMENT_SOUNDS),
            )
        }
    }
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        BackpackActivity::class.java,
        BackpackActivity.EXTRA_FRAGMENT_POSITION,
        fragmentId
    )
    private val backpackManager by KoinJavaComponent.inject(BackpackListManager::class.java)
    private lateinit var spriteController: SpriteController
    private lateinit var looksController: LookController
    private lateinit var sprite: Sprite
    private lateinit var look: LookData

    private lateinit var project: Project
    //private val waitThreshold: Long = 5000

    @Before
    fun setUp() {
        spriteController = SpriteController()
        looksController = LookController()
        project = UiTestUtils.createDefaultTestProject("testProject")
        addObjectToBackpack()
        baseActivityTestRule.launchActivity()

    }
    @After
    fun tearDown() {
        TestUtils.clearBackPack(backpackManager)
    }
    @Test
    fun testSimpleUndo() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.delete)).perform(ViewActions.click())
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .performCheckItemClick()
        onView(withId(R.id.confirm)).perform(ViewActions.click())

        Assert.assertNotEquals(0, backpackManager.sprites.size)


        // at this point you are in the backpack and the object is in there
        // then you can perform delete and undo and check whether it worked or not
    }
    private fun addObjectToBackpack() {
        when (fragmentId) {
            BackpackActivity.FRAGMENT_SPRITES -> {
                // Erstelle ein Sprite-Objekt und füge es zum Rucksack hinzu
                sprite = project.defaultScene.spriteList[1]
                backpackManager.sprites.add(spriteController.pack(sprite))
                backpackManager.saveBackpack()
            }
            /*BackpackActivity.FRAGMENT_LOOKS -> {
                // Erstelle ein Sprite-Objekt und füge es zum Rucksack hinzu
                look = project.defaultScene.spriteList[1].lookList[0]
                backpackManager.backpackedLooks.add(looksController.pack(look))
                backpackManager.saveBackpack()
            }*/
        }
            // ...
    }
}
