package ru.ydn.orienteer.components.structuretable;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class AbstractStructureTableToolbar<T> extends Panel
{
	private static final long serialVersionUID = 1L;

	private final StructureTable<T> table;

	/**
	 * Constructor
	 * 
	 * @param model
	 *            model
	 * @param table
	 *            data table this toolbar will be attached to
	 */
	public AbstractStructureTableToolbar(final IModel<?> model, final StructureTable<T> table)
	{
		super(table.newToolbarId(), model);
		this.table = table;
	}

	/**
	 * Constructor
	 * 
	 * @param table
	 *            data table this toolbar will be attached to
	 */
	public AbstractStructureTableToolbar(final StructureTable<T> table)
	{
		this(null, table);
	}

	/**
	 * @return DataTable this toolbar is attached to
	 */
	protected StructureTable<?> getTable()
	{
		return table;
	}
}