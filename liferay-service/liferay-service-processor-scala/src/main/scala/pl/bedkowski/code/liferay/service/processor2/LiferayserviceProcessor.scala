package pl.bedkowski.code.liferay.service.processor2

import java.net.URL
import java.util.Properties
import java.util.Set
import scala.collection.JavaConversions._
import org.apache.velocity.app.VelocityEngine
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element._
import pl.bedkowski.code.liferay.service.annotation.LiferayService
import javax.tools.Diagnostic
import org.apache.velocity.VelocityContext
import scala.collection.mutable.HashMap

class LiferayserviceProcessor(props:Properties) extends AbstractProcessor {

  private var ve:VelocityEngine = new VelocityEngine(props);
  {
    ve.init()
  }
  
  def this() = {
    this({
      var props = new Properties();
      val url = getClass().getClassLoader().getResource("velocity.properties");
      props.load(url.openStream());
      props
    })
  }
  
  def process(t: Set[_ <: TypeElement], env: RoundEnvironment) = {
    val types = env.getElementsAnnotatedWith(classOf[LiferayService])
    for(e <- types) {
      e.getKind() match {
        case ElementKind.INTERFACE => {
          val ls = e.getAnnotation(classOf[LiferayService])
          processIface(e.asInstanceOf[TypeElement], ls.initMethod(), ls.value());
        }
        case _ => processingEnv.getMessager().printMessage(
	                Diagnostic.Kind.ERROR,
	                "LiferayClp can only be applied to interface, found: " + {e match { 
	                  	case te:TypeElement => te.getQualifiedName()
	                  	case _ => e.getSimpleName()
	                 }}, 
	                e
        		)
      }
    }
    true
  }
  
  def processIface(iface:TypeElement, initMethod:String, classLoader:String) = {
    val vc = new VelocityContext();
    
    vc.put("className", iface.getSimpleName())
    vc.put("methods", getMethods(iface))
    vc.put("classLoader", classLoader)
    vc.put("initMethod", initMethod)
  }
  
  def getMethods(iface:TypeElement) = {
    val methods = new HashMap[String, Element];
    val members = processingEnv.getElementUtils().getAllMembers(iface)
    
    for(member <- members) {
      member.getKind() match {
        case ElementKind.METHOD => {
          if (isNative(member) || isAbstract(member)) {
            continue
          }
        }
      }
    }
    
    methods
  }
  
  private def isNative(elem:Element) = elem.getModifiers().contains(Modifier.NATIVE)
  private def isAbstract(elem:Element) = elem.getModifiers().contains(Modifier.ABSTRACT)

}