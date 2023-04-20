import korlibs.korge.scene.*
import korlibs.korge.view.*
//import korlibs.korge.view.ktree.*
import korlibs.io.file.std.*
import korlibs.korge.box2d.*
import korlibs.math.geom.Anchor
import korlibs.math.geom.Size
import org.jbox2d.dynamics.BodyType

class MainBox2d : Scene() {
    override suspend fun SContainer.sceneMain() {
        //registerBox2dSupportOnce()
        //addChild(resourcesVfs["restitution.ktree"].readKTree(views))
        //registerBox2dSupportOnce()

        solidRect(920, 100).xy(0, 620).registerBodyWithFixture(type = BodyType.STATIC, friction = 0.2, restitution = 0.2)
        for (n in 0 until 5) {
            //fastEllipse(Size(100, 100))
            circle(50f)
            //ellipse(Size(50, 50))
                .xy(120 + 140 * n, 246)
                .anchor(Anchor.CENTER)
                .registerBodyWithFixture(
                    type = BodyType.DYNAMIC,
                    linearVelocityY = 6.0,
                    friction = 0.2,
                    restitution = 0.3 + (n * 0.1)
                )
        }
    }
}

/*
suspend fun main() = Korge(width = 920, height = 720, quality = GameWindow.Quality.PERFORMANCE, title = "My Awesome Box2D Game!") {
    registerBox2dSupportOnce()
    addChild(resourcesVfs["restitution.ktree"].readKTree(views))
}

 */
