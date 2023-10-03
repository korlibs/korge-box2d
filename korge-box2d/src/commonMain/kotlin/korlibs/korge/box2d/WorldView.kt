package korlibs.korge.box2d

import korlibs.datastructure.*
import korlibs.time.milliseconds
import korlibs.time.seconds
import korlibs.korge.component.*
import korlibs.korge.view.*
import korlibs.korge.view.Circle
import korlibs.io.lang.*
import korlibs.io.serialization.xml.*
import korlibs.korge.view.Ellipse
import korlibs.math.geom.*
import korlibs.time.TimeSpan
import org.jbox2d.collision.shapes.*
import org.jbox2d.common.*
import org.jbox2d.dynamics.*
import org.jbox2d.userdata.*
import kotlin.native.concurrent.ThreadLocal

@PublishedApi
internal val DEFAULT_SCALE = 20.0
@PublishedApi
internal val DEFAULT_GRAVITY_Y = 9.8f

@ThreadLocal
var Views.registeredBox2dSupport: Boolean by Extra.Property { false }

fun Views.checkBox2dRegistered() {
    if (!registeredBox2dSupport) error("You should call Views.registerBox2dSupport()")
}

var World.component: Box2dWorldComponent?
    get() = get(Box2dWorldComponent.Key)
    set(value) {
        set(Box2dWorldComponent.Key, value)
    }

class Box2dWorldComponent(
    val worldView: View,
    override val world: World,
    var velocityIterations: Int = 6,
    var positionIterations: Int = 2,
    var autoDestroyBodies: Boolean = true,
    val step: TimeSpan = 16.milliseconds
) : WorldRef {
    var updater = worldView.addFixedUpdater(step) {
        update(step)
    }

    init {
        world.component = this
    }

    object Key : Box2dTypedUserData.Key<Box2dWorldComponent>()

    private val tempVec = Vec2()
    private val tempPos = MPoint()
    private fun update(step: TimeSpan) {
        world.step(step.seconds.toFloat(), velocityIterations, positionIterations)
        world.forEachBody { node ->
            val view = node.view

            if (view != null) {
                val worldScale = world.customScale
                val worldScaleInv = 1.0 / worldScale

                //val viewPos = view.getPositionRelativeTo(worldView, tempPos)
                val viewPos = tempPos.setTo(view.x, view.y)

                if (viewPos.x != node.viewInfo.x || viewPos.y != node.viewInfo.y || view.rotation != node.viewInfo.rotation) {
                    node.setTransform(
                        tempVec.set(viewPos.x * worldScaleInv, viewPos.y * worldScaleInv),
                        view.rotation
                    )
                    if (!node.viewInfo.firstFrame) {
                        node.linearVelocity = tempVec.set(0f, 0f)
                        node.angularVelocity = 0f
                        node.isActive = true
                        node.isAwake = true
                    }
                }

                val newX = node.position.x.toDouble() * worldScale
                val newY = node.position.y.toDouble() * worldScale

                view.position(newX, newY)
                //view.setPositionRelativeTo(worldView, tempPos.setTo(newX, newY))

                view.rotation = node.angle

                val viewRoot = view.root
                val viewRootStage = viewRoot is Stage

                node.viewInfo.x = view.x.toDouble()
                node.viewInfo.y = view.y.toDouble()
                node.viewInfo.rotation = view.rotation
                node.viewInfo.firstFrame = false

                if (autoDestroyBodies && node.viewInfo.onStage && !viewRootStage) {
                    world.destroyBody(node)
                    node.view?.body = null
                    node.view = null
                }

                node.viewInfo.onStage = viewRootStage
            }
        }
    }
}

@ThreadLocal
var View.box2dWorldComponent by Extra.PropertyThis<View, Box2dWorldComponent?> { null }

fun View.getOrCreateBox2dWorld(): Box2dWorldComponent {
    if (this.box2dWorldComponent == null) {
        this.box2dWorldComponent = Box2dWorldComponent(this, World(0f, DEFAULT_GRAVITY_Y).also { it.customScale = DEFAULT_SCALE }, 6, 2)
    }
    return this.box2dWorldComponent!!
}

val View.nearestBox2dWorldComponent: Box2dWorldComponent
    get() {
        var nearestReference: View? = null
        var view: View? = this
        while (view != null) {
            val component = view.box2dWorldComponent
            if (component != null) {
                return component
            }
            //if (view.parent == null || view is View.Reference) {
            if (view is View.Reference) {
                if (nearestReference == null) {
                    nearestReference = view
                }
            }
            if (view.parent == null) {
                return (nearestReference ?: view).getOrCreateBox2dWorld()
            }
            view = view.parent
        }
        invalidOp
    }

val View.nearestBox2dWorld: World get() = nearestBox2dWorldComponent.world

inline fun View.createBody(world: World? = null, callback: BodyDef.() -> Unit): Body = (world ?: nearestBox2dWorld).createBody(BodyDef().apply(callback))

/** Shortcut to create and attach a [Fixture] to this [Body] */
inline fun Body.fixture(callback: FixtureDef.() -> Unit): Body = this.also { createFixture(FixtureDef().apply(callback)) }

@ThreadLocal
var View.body by Extra.PropertyThis<View, Body?>("box2dBody") { null }

inline fun <T : View> T.registerBody(body: Body): T {
    body.view = this
    this.body = body
    return this
}

//private val BOX2D_BODY_KEY = "box2dBody"

private val ViewKey = Box2dTypedUserData.Key<View>()

var Body.view: View?
    get() = this[ViewKey]
    set(value) {
        this[ViewKey] = value
    }

/** Shortcut to create a simple [Body] to this [World] with the specified properties */
inline fun <T : View> T.registerBodyWithFixture(
    angularVelocity: Number = 0.0,
    linearVelocityX: Number = 0.0,
    linearVelocityY: Number = 0.0,
    linearDamping: Number = 0.0,
    angularDamping: Number = 0.0,
    gravityScale: Number = 1.0,
    shape: Shape? = null,
    allowSleep: Boolean = true,
    awake: Boolean = true,
    fixedRotation: Boolean = false,
    bullet: Boolean = false,
    type: BodyType = BodyType.STATIC,
    friction: Number = 0.2,
    restitution: Number = 0.2,
    active: Boolean = true,
    isSensor: Boolean = false,
    density: Number = 1.0,
    world: World? = null,
): T {
    val view = this

    val body = createBody(world) {
        this.type = type
        this.angle = rotation
        this.angularVelocity = angularVelocity.toFloat()
        this.position.set(x.toFloat(), y.toFloat())
        this.linearVelocity.set(linearVelocityX.toFloat(), linearVelocityY.toFloat())
        this.linearDamping = linearDamping.toFloat()
        this.angularDamping = angularDamping.toFloat()
        this.gravityScale = gravityScale.toFloat()
        this.allowSleep = allowSleep
        this.fixedRotation = fixedRotation
        this.bullet = bullet
        this.awake = awake
        this.active = active
    }
    val world = body.world

    body.fixture {
        this.shape = shape ?:
            when {
                view is Circle -> CircleShape(view.radius / world.customScale)
                view is Ellipse && view.isCircle -> CircleShape(view.radius.width / world.customScale)
                else -> BoxShape(getLocalBounds() / world.customScale)
            }

        //BoxShape(width / world.customScale, height / world.customScale)
        this.isSensor = isSensor
        this.friction = friction.toFloat()
        this.restitution = restitution.toFloat()
        this.density = density.toFloat()
    }
    //body._linearVelocity.set(linearVelocityX.toFloat(), linearVelocityY.toFloat())
    body.view = this
    this.body = body
    return this
}

fun BoxShape(rect: Rectangle) = PolygonShape().apply {
    count = 4
    vertices[0].set(rect.left, rect.top)
    vertices[1].set(rect.right, rect.top)
    vertices[2].set(rect.right, rect.bottom)
    vertices[3].set(rect.left, rect.bottom)
    normals[0].set(0.0f, -1.0f)
    normals[1].set(1.0f, 0.0f)
    normals[2].set(0.0f, 1.0f)
    normals[3].set(-1.0f, 0.0f)
    centroid.setZero()
}

/**
 * Creates a [PolygonShape] as a box with the specified [width] and [height]
 */
inline fun BoxShape(width: Number, height: Number) = PolygonShape().apply {
    count = 4
    vertices[0].set(0, 0)
    vertices[1].set(width, 0)
    vertices[2].set(width, height)
    vertices[3].set(0, height)
    normals[0].set(0.0f, -1.0f)
    normals[1].set(1.0f, 0.0f)
    normals[2].set(0.0f, 1.0f)
    normals[3].set(-1.0f, 0.0f)
    centroid.setZero()
}

inline fun Container.worldView(
    gravityX: Number = 0.0,
    gravityY: Number = DEFAULT_GRAVITY_Y.toDouble(),
    velocityIterations: Int = 6,
    positionIterations: Int = 2,
    callback: @ViewDslMarker Container.() -> Unit = {}
): Container = container(callback = callback).also {
    it.getOrCreateBox2dWorld().also {
        it.world.gravity.set(gravityX, gravityY)
        it.velocityIterations = velocityIterations
        it.positionIterations = positionIterations
    }
}
