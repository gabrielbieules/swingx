/*
 * Created on 07.06.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListModel;

import org.jdesktop.swingx.action.EditorPaneLinkVisitor;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ConditionalHighlighter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.ShuttleSorter;
import org.jdesktop.swingx.decorator.Sorter;

/**
 * @author Jeanette Winzenburg
 */
public class JXListTest extends InteractiveTestCase {

    private ListModel listModel;
    private DefaultListModel ascendingListModel;

    public void testEmptyFilter() {
        JXList list = new JXList();
        list.setModel(ascendingListModel);
        assertEquals(ascendingListModel.getSize(), list.getModelSize());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
    }
    
    public void testFilterEnabled() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        assertNotSame(ascendingListModel, list.getModel());
        assertEquals(ascendingListModel.getSize(), list.getModelSize());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
        
    }

    public void testFilterEnabledAndDisabled() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        Sorter sorter = new ShuttleSorter(0, false);
        FilterPipeline pipeline = list.getFilters();
        pipeline.setSorter(sorter);
        list.setFilterEnabled(false);
        assertSame(ascendingListModel, list.getModel());
        assertEquals(ascendingListModel.getSize(), list.getModelSize());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
        
    }
    public void testSortingFilterEnabled() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        Sorter sorter = new ShuttleSorter(0, false);
        FilterPipeline pipeline = list.getFilters();
        assertNotNull(pipeline);
        pipeline.setSorter(sorter);
        assertEquals(ascendingListModel.getSize(), list.getModelSize());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(list.getModelSize() - 1));
        
    }
    
    public void testSortingKeepsModelSelection() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        list.setSelectedIndex(0);
        Sorter sorter = new ShuttleSorter(0, false);
        FilterPipeline pipeline = list.getFilters();
        pipeline.setSorter(sorter);
        assertEquals("last row must be selected after sorting", 
                ascendingListModel.getSize() - 1, list.getSelectedIndex());
    }

    public void testSelectionAfterDeleteAbove() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        list.setSelectedIndex(1);
        ascendingListModel.remove(0);
        assertEquals("first row must be selected removing old first", 
                0, list.getSelectedIndex());
        
    }
    public void testSortingFilterDisabled() {
        JXList list = new JXList();
        list.setModel(ascendingListModel);
        Sorter sorter = new ShuttleSorter(0, false);
        FilterPipeline pipeline = list.getFilters();
        assertNotNull(pipeline);
        pipeline.setSorter(sorter);
        assertSame(ascendingListModel, list.getModel());
        assertEquals(ascendingListModel.getSize(), list.getModelSize());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
        
    }

    public void interactiveTestSorter() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        final Sorter sorter = new ShuttleSorter(0, false);
        FilterPipeline pipeline = list.getFilters();
        pipeline.setSorter(sorter);
        Action action = new AbstractAction("Toggle Sort Order") {

            public void actionPerformed(ActionEvent e) {
                sorter.setAscending(!sorter.isAscending());
                
            }
            
        };
        JFrame frame = wrapWithScrollingInFrame(list, "Toggle sorter");
        addAction(frame, action);
        frame.setVisible(true);
        
    }
    
    public void interactiveTestCompareFocusedCellBackground() {
        JXList xlist = new JXList(listModel);
        xlist.setBackground(new Color(0xF5, 0xFF, 0xF5));
        JList list = new JList(listModel);
        list.setBackground(new Color(0xF5, 0xFF, 0xF5));
        JFrame frame = wrapWithScrollingInFrame(xlist, list, "unselectedd focused background: JXList/JList");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter5() {
        JXList list = new JXList(listModel);
        String pattern = "Row";
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            new PatternHighlighter(null, Color.red, pattern, 0, 1),
        }));
        JFrame frame = wrapWithScrollingInFrame(list, "PatternHighlighter: " + pattern);
        frame.setVisible(true);
    }

    public void interactiveTestTableAlternateHighlighter1() {
        JXList list = new JXList(listModel);
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            AlternateRowHighlighter.
            linePrinter,
        }));

        JFrame frame = wrapWithScrollingInFrame(list, "AlternateRowHighlighter - lineprinter");
        frame.setVisible(true);
    }

    public void interactiveTestRolloverHighlight() {
        JXList list = new JXList(listModel);
    //    table.setLinkVisitor(new EditorPaneLinkVisitor());
        list.setRolloverEnabled(true);
        Highlighter conditional = new ConditionalHighlighter(
                new Color(0xF0, 0xF0, 0xE0), null, -1, -1) {

            protected boolean test(ComponentAdapter adapter) {
                Point p = (Point) adapter.getComponent().getClientProperty(RolloverProducer.ROLLOVER_KEY);
     
                return p != null &&  p.y == adapter.row;
            }
            
        };
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {conditional }));
        JFrame frame = wrapWithScrollingInFrame(list, "rollover highlight");
        frame.setVisible(true);

    }

    /**
     * Issue #20: Highlighters and LinkRenderers don't work together
     *
     */
    public void interactiveTestRolloverHighlightAndLink() {
        JXList list = new JXList(createListModelWithLinks());
        list.setLinkVisitor(new EditorPaneLinkVisitor());
    //    table.setRolloverEnabled(true);
        Highlighter conditional = new ConditionalHighlighter(
                new Color(0xF0, 0xF0, 0xE0), null, -1, -1) {

            protected boolean test(ComponentAdapter adapter) {
                Point p = (Point) adapter.getComponent().getClientProperty(RolloverProducer.ROLLOVER_KEY);
     
                return p != null &&  p.y == adapter.row;
            }
            
        };
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {conditional }));
        JFrame frame = wrapWithScrollingInFrame(list, "rollover highlight with links");
        frame.setVisible(true);

    }
    private ListModel createListModel() {
        JXList list = new JXList();
        return new DefaultComboBoxModel(list.getActionMap().allKeys());
    }

    private DefaultListModel createAscendingListModel(int startRow, int count) {
        DefaultListModel l = new DefaultListModel();
        for (int row = startRow; row < startRow  + count; row++) {
            l.addElement(new Integer(row));
        }
        return l;
    }
    private DefaultListModel createListModelWithLinks() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 20; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a resource", null, url);
                }
                model.addElement(link);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
 
        return model;
    }

    protected void setUp() throws Exception {
        super.setUp();
        listModel = createListModel();
        ascendingListModel = createAscendingListModel(0, 20);
    }
    public JXListTest() {
        super("JXList Tests");
    }

    
    public static void main(String[] args) {
        setSystemLF(true);
        JXListTest test = new JXListTest();
        try {
         // test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Column.*");
         //   test.runInteractiveTests("interactive.*TableHeader.*");
         //   test.runInteractiveTests("interactive.*Render.*");
            test.runInteractiveTests("interactive.*Sort.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
}
