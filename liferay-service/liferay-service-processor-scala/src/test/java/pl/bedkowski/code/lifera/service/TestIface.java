package pl.bedkowski.code.lifera.service;

import pl.bedkowski.code.liferay.service.annotation.LiferayService;


@LiferayService
public interface TestIface {


	String myMethod();
	
	String myMethod(String s);
}
