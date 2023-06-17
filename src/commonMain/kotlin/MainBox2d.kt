import korlibs.korge.box2d.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.math.geom.Anchor
import org.jbox2d.dynamics.BodyType

class MainBox2d : Scene() {
    override suspend fun SContainer.sceneMain() {
        //registerBox2dSupportOnce()
        //addChild(resourcesVfs["restitution.ktree"].readKTree(views))
        //registerBox2dSupportOnce()

        solidRect(920, 100).xy(0, 620).registerBodyWithFixture(type = BodyType.STATIC, friction = 0.2, restitution = 0.2)
        for (n in 0 until 5) {
            //fastEllipse(Size(100, 100))
            val c = circle(50f)
                //ellipse(Size(50, 50))
                .xy(120 + 140 * n, 246)
                .anchor(Anchor.CENTER)
                .registerBodyWithFixture(
                    type = BodyType.DYNAMIC,
                    linearVelocityX = n * 2.0,
                    friction = 0.2,
                    restitution = 0.3 + (n * 0.1)
                )
            if (n == 0) {
                c.body!!._linearVelocity.x = 16f
            }
        }
    }
}

/*
suspend fun main() = Korge(width = 920, height = 720, quality = GameWindow.Quality.PERFORMANCE, title = "My Awesome Box2D Game!") {
    registerBox2dSupportOnce()
    addChild(resourcesVfs["restitution.ktree"].readKTree(views))
}

 */