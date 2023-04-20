package korlibs.korge.box2d

/*
import korlibs.korge.view.ktree.*

@ThreadLocal
var KTreeSerializer.box2dWorld by Extra.PropertyThis<KTreeSerializer, Box2dWorldComponent?> { null }

object PhysicsKTreeSerializerExtension : KTreeSerializerExtension("physics") {
    override fun complete(serializer: KTreeSerializer, view: View) {
        //serializer.box2dWorld?.world?.forEachBody { println("it.linearVelocityY: ${it.linearVelocityY}") }
        serializer.box2dWorld?.update(0.0.milliseconds)
        serializer.box2dWorld?.world?.forEachBody {
            if (!it.didReset) {
                it.didReset = true
                it.type = it.bodyDef.type
                it.linearVelocityX = it.bodyDef.linearVelocity.x
                it.linearVelocityY = it.bodyDef.linearVelocity.y
                it.gravityScale = it.bodyDef.gravityScale
                it.angularVelocity = it.bodyDef.angularVelocity
                it.isSleepingAllowed = it.bodyDef.allowSleep
                it.isAwake = it.bodyDef.awake
                it.isFixedRotation = it.bodyDef.fixedRotation
                it.isBullet = it.bodyDef.bullet
            }
            //println("it.linearVelocityY: ${it.linearVelocityY}")
        }
    }

    override fun setProps(serializer: KTreeSerializer, view: View, xml: Xml) {
        //println("PhysicsKTreeSerializerExtension.setProps")
        val body = view.registerBodyWithFixture(
            world = serializer.box2dWorld?.world,
            type = xml.strNull("type")?.let { BodyType[it] } ?: BodyType.STATIC,
            linearVelocityX = xml.float("linearVelocityX", 0f),
            linearVelocityY = xml.float("linearVelocityY", 0f),
            gravityScale = xml.float("gravityScale", 1f),
            angularVelocity = xml.float("angularVelocity", 0f),
            allowSleep = xml.boolean("isSleepingAllowed", true),
            awake = xml.boolean("isAwake", true),
            fixedRotation = xml.boolean("isFixedRotation", false),
            bullet = xml.boolean("isBullet", false),
            friction = xml.float("friction", 0f),
            density = xml.float("density", 1f),
            restitution = xml.float("restitution", 0f),
            isSensor = xml.boolean("isSensor", false),
            active = xml.boolean("isActive", true)
        ).body
        body?.didReset = false
    }

    override fun getProps(serializer: KTreeSerializer, view: View): Map<String, Any?>? {
        val body = view.body ?: return null
        val fixture = body.m_fixtureList
        //println("PhysicsKTreeSerializerExtension.getProps")
        return LinkedHashMap<String, Any?>().apply {
            if (body.type != BodyType.STATIC) this["type"] = body.type
            if (body.linearVelocityX != 0f) this["linearVelocityX"] = body.linearVelocityX
            if (body.linearVelocityY != 0f) this["linearVelocityY"] = body.linearVelocityY
            if (body.gravityScale != 1f) this["gravityScale"] = body.gravityScale
            if (body.angularVelocity != 0f) this["angularVelocity"] = body.angularVelocity
            if (!body.isSleepingAllowed) this["isSleepingAllowed"] = body.isSleepingAllowed
            if (!body.isAwake) this["isAwake"] = body.isAwake
            if (body.isFixedRotation) this["isFixedRotation"] = body.isFixedRotation
            if (body.isBullet) this["isBullet"] = body.isBullet
            if (!body.isActive) this["isActive"] = body.isActive
            if (fixture != null) {
                if (fixture.isSensor) this["isSensor"] = fixture.isSensor
                if (fixture.friction != 0f) this["friction"] = fixture.friction
                if (fixture.density != 1f) this["density"] = fixture.density
                if (fixture.restitution != 0f) this["restitution"] = fixture.restitution
            }
        }
    }
}
fun ViewsContainer.registerBox2dSupportOnce() {
    if (views.registeredBox2dSupport) return
    views.registeredBox2dSupport = true
    views.ktreeSerializer.registerExtension(PhysicsKTreeSerializerExtension)
    views.viewExtraBuildDebugComponent.add { views, view, container ->
        val physicsContainer = container.container {
        }
        fun physicsContainer() {
            physicsContainer.removeChildren()
            val body = view.body
            if (body != null) {
                physicsContainer.uiCollapsibleSection("Box2D Physics") {
                    button("Remove") {
                        onClick {
                            body.destroyBody()
                            view.body = null
                            body.view = null
                            physicsContainer()
                        }
                    }
                    uiEditableValue(body::type)
                    val fixture = body.m_fixtureList
                    if (fixture != null) {
                        uiEditableValue(fixture::isSensor)
                        uiEditableValue(fixture::friction)
                        uiEditableValue(fixture::density, min = 0f, clampMin = true, clampMax = false)
                        uiEditableValue(fixture::restitution)
                    }
                    uiEditableValue(body::linearVelocityX, min = -100f, max = 100f, clampMin = true, clampMax = false)
                    uiEditableValue(body::linearVelocityY, min = -100f, max = 100f, clampMin = true, clampMax = false)
                    uiEditableValue(body::gravityScale, min = -100f, max = 100f, clampMin = true, clampMax = false)
                    uiEditableValue(body::angularVelocity)
                    uiEditableValue(body::isSleepingAllowed)
                    uiEditableValue(body::isAwake)
                    uiEditableValue(body::isFixedRotation)
                    uiEditableValue(body::isBullet)
                    uiEditableValue(body::isActive)
                }
            } else {
                physicsContainer.button("Add box2d physics") {
                    onClick {
                        view.registerBodyWithFixture(type = BodyType.STATIC)
                        views.debugSaveView("Add physics", view)
                        physicsContainer()
                    }
                }
            }
            physicsContainer.root?.relayout()
        }
        physicsContainer()
    }
    //views.serializer.register()
}
*/