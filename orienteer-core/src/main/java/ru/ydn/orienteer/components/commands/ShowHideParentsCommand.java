package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.table.OrienteerDataTable;

public class ShowHideParentsCommand<T> extends AjaxCommand<T>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IModel<OClass> classModel;
	private IModel<Boolean> showHideParentModel;

	public ShowHideParentsCommand(IModel<OClass> classModel, OrienteerDataTable<T, ?> table, IModel<Boolean> showHideParentModel)
	{
		super(new StringResourceModel("command.showhide.${}", showHideParentModel), table);
		this.classModel = classModel;
		this.showHideParentModel = showHideParentModel;
		setIcon(FAIconType.reorder);
		setBootstrapType(BootstrapType.INFO);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(classModel!=null)
		{
			OClass oClass = classModel.getObject();
			setVisible(oClass!=null && oClass.getSuperClass()!=null);
		}
	}



	@Override
	public void onClick(AjaxRequestTarget target) {
		Boolean current = showHideParentModel.getObject();
		current = current!=null?!current:true;
		showHideParentModel.setObject(current);
		send(this, Broadcast.BUBBLE, target);
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if(classModel!=null) classModel.detach();
	}
	
	

}
