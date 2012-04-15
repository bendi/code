package pl.bedkowski.code.tapestry.pages;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import pl.bedkowski.code.tapestry.annotation.SelectModel;
import pl.bedkowski.code.tapestry.model.Color;

import com.google.common.collect.Lists;

/**
 * Start page of application tapestry.
 */
public class Index {
	@SuppressWarnings("unused")
	@Property
	@Inject
	@Symbol(SymbolConstants.TAPESTRY_VERSION)
	private String tapestryVersion;

	@InjectComponent
	private Zone zone;

	@Persist
	@Property
	private int clickCount;

	@Inject
	private AlertManager alertManager;

	@SuppressWarnings("unused")
	@SelectModel(labelProperty = "label")
	private List<Color> colorModel;

	@SuppressWarnings("unused")
	@Property
	private Color selectedColor;

	@SetupRender
	void init() {
		colorModel = Lists.newArrayList(Color.COLORS.values());
	}

	public Date getCurrentTime() {
		return new Date();
	}

	void onActionFromIncrement() {
		alertManager.info("Increment clicked");

		clickCount++;
	}

	Object onActionFromIncrementAjax() {
		clickCount++;

		alertManager.info("Increment (via Ajax) clicked");

		return zone;
	}

	public ValueEncoder<Color> getColorEncoder() {

		return new ValueEncoder<Color>() {

			@Override
			public String toClient(Color value) {
				// return the given object's ID
				return String.valueOf(value.getId());
			}

			@Override
			public Color toValue(String id) {
				return Color.COLORS.get(Long.valueOf(id));
			}
		};
	}
}
