package korlibs.korge.box2d

import korlibs.korge.tests.ViewsForTesting
import korlibs.korge.view.*
import korlibs.math.geom.Anchor
import korlibs.time.milliseconds
import org.jbox2d.dynamics.BodyType
import kotlin.test.Test
import kotlin.test.assertEquals

class Box2dTest : ViewsForTesting() {
	@Test
	fun testSetInitialLinearVelocity() {
		val container = Container()
		val rect = container
			.solidRect(920, 100).xy(0, 620)
			.registerBodyWithFixture(type = BodyType.STATIC, friction = 0.2, restitution = 0.2)
		val n = 0
		val c = container.circle(50f)
			//ellipse(Size(50, 50))
			.xy(120 + 140 * n, 246)
			.anchor(Anchor.CENTER)
			.registerBodyWithFixture(
				type = BodyType.DYNAMIC,
				linearVelocityX = 16.0,
				friction = 0.2,
				restitution = 0.3 + (n * 0.1)
			)
		c.body!!._linearVelocity.x = 16f
		assertEquals(120.0, c.pos.x.toDouble(), 0.1)
		assertEquals(246.0, c.pos.y.toDouble(), 0.1)
		container.updateSingleView(1000.milliseconds)
		assertEquals(196.79994, c.pos.x.toDouble(), 0.1)
		assertEquals(252.77376, c.pos.y.toDouble(), 0.1)
	}

	@Test
	fun test() = viewsTest {
        /*
		lateinit var body: Body

		val view = worldView {
			createBody {
				setPosition(0, -10)
			}.fixture {
				shape = BoxShape(100, 20)
				density = 0f
			}.setView(graphics {
				fill(Colors.RED) {
					rect(-50f, -10f, 100f, 20f)
					//anchor(0.5, 0.5)
				}
			})

			// Dynamic Body
			body = createBody {
				type = BodyType.DYNAMIC
				setPosition(0, 10)
			}.fixture {
				shape = BoxShape(2f, 2f)
				density = 1f
				friction = .2f
			}.setView(solidRect(2f, 2f, Colors.GREEN).anchor(.5, .5))
		}

		assertEquals(10f, body.position.y)
		for (n in 0 until 40) view.updateSingleView(16.milliseconds)
		assertEquals(true, body.position.y < 8f)
         */
	}
}
