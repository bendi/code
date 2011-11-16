package pl.bedkowski.code.criteriautils;


import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import com.google.common.collect.Lists;

public class CriteriaUtils {

	/**
	 *
	 * @param <T>
	 * @param clz
	 * @param dates
	 * @param client
	 * @param entityManager
	 * @return
	 */
	public static <T> T getSummaries(Class<T> clz, Criterion dates, EntityManager entityManager, String... skipProperties) {
		return getSummaries(clz, null, dates, entityManager, skipProperties);
	}

	/**
	 *
	 * @param <T>
	 * @param clz
	 * @param ord
	 * @param dates
	 * @param entityManager
	 * @param skipProperties
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSummaries(Class<T> clz, Order ord, Criterion dates, EntityManager entityManager, String... skipProperties) {
		Session sess = (Session) entityManager.getDelegate();
		Criteria crit = sess.createCriteria(clz);
		List<String> skipPropertiesList = Lists.newArrayList("id", "date");
		if (skipProperties != null) {
			skipPropertiesList.addAll(Arrays.asList(skipProperties));
		}
		crit.setProjection(getSumarriesProjectionList(clz, skipPropertiesList));
		crit.add(dates);
		if (ord != null) {
			crit.addOrder(ord);
		}
		crit.setResultTransformer(new AliasToBeanResultTransformer(clz));
		T foundInstance = (T)crit.uniqueResult();

		return preventNPE(foundInstance, clz);
	}

	private static <T> T preventNPE(final T foundInstance, Class<T> clz) {
		return Enhancer.create(clz, new MethodInterceptor(){
			@Override
			public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				Object ret = method.invoke(foundInstance, args);
				String methodName = method.getName();
				if (methodName.startsWith("get") && BigDecimal.class.isAssignableFrom(method.getReturnType()) && ret == null) {
					ret = BigDecimal.ZERO;
				}
				return ret;
			}
		});
	}

	/**
	 *
	 * @param clz
	 * @param skipped
	 * @return
	 */
	public static ProjectionList getSumarriesProjectionList(Class<?> clz, List<String> skippedList) {
		ProjectionList projectionList = Projections.projectionList();
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clz);
		for(PropertyDescriptor dd : propertyDescriptors) {
			Method m = dd.getReadMethod();
			if (m == null || !m.isAnnotationPresent(Column.class)) {
				continue;
			}
			String propertyName = dd.getName();
			if (skippedList.contains(propertyName)) {
				continue;
			}
			projectionList.add(Projections.sum(propertyName), propertyName);
		}
		return projectionList;
	}

	public static ProjectionList getSumarriesProjectionList(Class<?> clz, String... skipped) {
		return getSumarriesProjectionList(clz, Arrays.asList(skipped));
	}
}
