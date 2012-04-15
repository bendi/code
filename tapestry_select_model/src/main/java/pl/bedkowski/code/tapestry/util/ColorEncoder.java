package pl.bedkowski.code.tapestry.util;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;

import pl.bedkowski.code.tapestry.model.Color;

public class ColorEncoder implements ValueEncoder<Color>, ValueEncoderFactory<Color> {

    @Override
    public String toClient(Color value) {
        // return the given object's ID
        return String.valueOf(value.getId());
    }

    @Override
    public Color toValue(String id) {
    	return Color.COLORS.get(Long.valueOf(id));
    }

    // let this ValueEncoder also serve as a ValueEncoderFactory
    @Override
    public ValueEncoder<Color> create(Class<Color> type) {
        return this;
    }
}