package org.osivia.portal.api.displaytag;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.TableDecorator;
import org.displaytag.model.HeaderCell;
import org.displaytag.model.TableModel;
import org.dom4j.Element;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;

/**
 * Sortable table decorator.
 * 
 * @author Cédric Krommenhoek
 * @see TableDecorator
 */
public class SortableTableDecorator extends TableDecorator {

    /**
     * Constructor.
     */
    public SortableTableDecorator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void init(PageContext pageContext, Object decorated, TableModel tableModel) {
        super.init(pageContext, decorated, tableModel);

        if (tableModel.isSorted()) {
            HeaderCell sortedHeaderCell = tableModel.getSortedColumnHeader();
            boolean ascending = tableModel.isSortOrderAscending();
            addSortedHeaderIcon(sortedHeaderCell, ascending);
        }
    }


    /**
     * Add sorted header cell icon.
     * 
     * @param sortedHeaderCell header cell
     * @param ascending ascending indicator
     */
    protected void addSortedHeaderIcon(HeaderCell sortedHeaderCell, boolean ascending) {
        String glyphicons;
        if (ascending) {
            glyphicons = "halflings sort-by-attributes";
        } else {
            glyphicons = "halflings sort-by-attributes-alt";
        }

        Element element = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, sortedHeaderCell.getTitle());
        element.add(DOM4JUtils.generateElement(HTMLConstants.SPAN, null, null, glyphicons, null));
        sortedHeaderCell.setTitle(DOM4JUtils.write(element));
    }

}
