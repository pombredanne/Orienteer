package ru.ydn.orienteer.components.properties;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.RangeValidator;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.components.properties.visualizers.IVisualizer;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
import ru.ydn.wicket.wicketorientdb.validation.OPropertyValueValidator;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentMetaPanel<V> extends AbstractModeMetaPanel<ODocument, DisplayMode, OProperty, V> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ODocumentMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<ODocument> entityModel, IModel<OProperty> propertyModel,
			IModel<V> valueModel)
	{
		super(id, modeModel, entityModel, propertyModel, valueModel);
	}

	public ODocumentMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<ODocument> entityModel, IModel<OProperty> propertyModel)
	{
		super(id, modeModel, entityModel, propertyModel);
	}
	
	
	@Override
	protected IModel<V> resolveValueModel() {
		return new DynamicPropertyValueModel<V>(getEntityModel(), getPropertyModel());
	}
	
	

	@Override
	protected void onPostResolveComponent(Component component, OProperty critery) {
		super.onPostResolveComponent(component, critery);
		
		if(component instanceof FormComponent)
		{
			if(critery.isNotNull()) ((FormComponent<?>)component).setRequired(true);
			((FormComponent<?>)component).add(new OPropertyValueValidator<Object>(critery));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Component resolveComponent(String id, DisplayMode mode,
			OProperty property) {
		if(mode.canModify() && property.isReadonly()) mode = DisplayMode.VIEW;
		OType oType = property.getType();
		UIVisualizersRegistry registry = OrienteerWebApplication.get().getUIVisualizersRegistry();
		String visualizationComponent = CustomAttributes.VISUALIZATION_TYPE.getValue(property);
		if(visualizationComponent!=null)
		{
			IVisualizer visualizer = registry.getComponentFactory(oType, visualizationComponent);
			if(visualizer!=null) 
			{
				Component ret = visualizer.createComponent(id, mode, getEntityModel(), getPropertyModel(), getModel());
				if(ret!=null) return ret;
			}
		}
		return registry.getComponentFactory(oType, IVisualizer.DEFAULT_VISUALIZER)
							.createComponent(id, mode, getEntityModel(), getPropertyModel(), getModel());
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Comparable<? super T> & Serializable> T toRangePoint(String str, Class<?> clazz)
	{
		if(Strings.isEmpty(str)) return null;
		try
		{
			Method method = clazz.getMethod("valueOf", String.class);
			return (T) method.invoke(null, str);
		} catch (Exception e)
		{
			return null;
		} 	
	}

	@Override
	public IModel<String> newLabelModel() {
		return new OPropertyNamingModel(getPropertyModel());
	}
	
}
