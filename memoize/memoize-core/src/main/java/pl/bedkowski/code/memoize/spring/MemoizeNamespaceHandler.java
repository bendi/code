package pl.bedkowski.code.memoize.spring;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MemoizeNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("use-method-cache", new UseMethodCacheTagParser());
    }
}