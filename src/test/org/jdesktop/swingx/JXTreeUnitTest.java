/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.TreeCellRenderer;

import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;

//import de.kleopatra.view.LFSwitcher;

/**
 * @author Jeanette Winzenburg
 */
public class JXTreeUnitTest extends InteractiveTestCase {

  	TreeTableModel treeTableModel;    // shared

    public JXTreeUnitTest() {
        super("JXTree Test");
    }

    /** 
     * Needed because testCase fails if it does not have at least 
     * one fixture. 
     * learning something new every day :-)
     *
     */
    public void testDummy() {
        
    }

 
    /**
     * Issue ??: Background highlighters not working on JXTree.
     *
     */
    public void interactiveTestHighlighters() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setCellRenderer(createHighlightingRenderer(tree.getCellRenderer()));
        tree.setHighlighters(new HighlighterPipeline(new Highlighter[] {
                new PatternHighlighter(null, Color.red, "S.*", 0, 1),
            }));
//        tree.setHighlighters(new HighlighterPipeline(
//                new Highlighter[] { AlternateRowHighlighter.classicLinePrinter, }));
        JFrame frame = wrapWithScrollingInFrame(tree, "tooltips");
        frame.setVisible(true);  // RG: Changed from deprecated method show();
        
    }
    
    
    private TreeCellRenderer createHighlightingRenderer(final TreeCellRenderer delegate) {
        TreeCellRenderer renderer = new TreeCellRenderer() {

            public Component getTreeCellRendererComponent(JTree tree, Object value, 
                    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component result = delegate.getTreeCellRendererComponent(tree, value, 
                        selected, expanded, leaf, row, hasFocus);
                if (tree instanceof JXTree) {
                    JXTree xTree = (JXTree) tree;
                    HighlighterPipeline highlighters = xTree.getHighlighters();
                    if (highlighters != null) {
                        xTree.getComponentAdapter().row = row;
                        System.out.println(result);
                        result = highlighters.apply(result, xTree.getComponentAdapter());
                    }
                }
                 return result;
            }
            
        };
        return renderer;
    }

    public void interactiveTestToolTips() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setCellRenderer(createRenderer(tree.getCellRenderer()));
        // JW: JTree does not automatically register itself
        // should JXTree? 
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(tree);
        JFrame frame = wrapWithScrollingInFrame(tree, "tooltips");
        frame.setVisible(true);  // RG: Changed from deprecated method show();

    }
    
    
    private TreeCellRenderer createRenderer(final TreeCellRenderer delegate) {
        TreeCellRenderer renderer = new TreeCellRenderer() {

            public Component getTreeCellRendererComponent(JTree tree, Object value, 
                    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component result = delegate.getTreeCellRendererComponent(tree, value, 
                        selected, expanded, leaf, row, hasFocus);
                ((JComponent) result).setToolTipText(String.valueOf(tree.getPathForRow(row)));
                 return result;
            }
            
        };
        return renderer;
    }

    /**
     * test if lineStyle client property is respected by JXTree.
     * Note that some LFs don't respect anyway (WinLF f.i.)
     */
    public void interactiveTestLineStyle() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setDragEnabled(true);
        tree.putClientProperty("JTree.lineStyle", "None");
        JFrame frame = wrapWithScrollingInFrame(tree, "LineStyle Test");
        frame.setVisible(true);  // RG: Changed from deprecated method show();
    }

    /**    
     * setting tree properties: JXTree is updated properly.
     */    
    public void interactiveTestTreeProperties() {
        final JXTree treeTable = new JXTree(treeTableModel);
        Action toggleHandles = new AbstractAction("Toggle Handles") {

            public void actionPerformed(ActionEvent e) {
                treeTable.setShowsRootHandles(!treeTable.getShowsRootHandles());
                
            }
            
        };
        Action toggleRoot = new AbstractAction("Toggle Root") {

            public void actionPerformed(ActionEvent e) {
                treeTable.setRootVisible(!treeTable.isRootVisible());
                
            }
            
        };
        treeTable.setRowHeight(22);
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "Toggle Tree properties ");
        addAction(frame, toggleRoot);
        addAction(frame, toggleHandles);
        frame.setVisible(true);
    }
    
    /**    
     * setting tree properties: scrollsOnExpand.
     * does nothing...
     * 
     */    
    public void interactiveTestTreeExpand() {
        final JXTree treeTable = new JXTree(treeTableModel);
        Action toggleScrolls = new AbstractAction("Toggle Scroll") {

            public void actionPerformed(ActionEvent e) {
                treeTable.setScrollsOnExpand(!treeTable.getScrollsOnExpand());
                
            }
            
        };
         Action expand = new AbstractAction("Expand") {

            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = treeTable.getSelectionRows();
                if (selectedRows.length > 0) {
                    treeTable.expandRow(selectedRows[0]);
                }
               
            }
            
        };
 
        treeTable.setRowHeight(22);
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "Toggle Tree expand properties ");
        addAction(frame, toggleScrolls);
        addAction(frame, expand);
        frame.setVisible(true);
    }
    

    
    /**
     * test if showsRootHandles client property is respected by JXTree.
     */
    public void interactiveTestShowsRootHandles() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setShowsRootHandles(false);
        tree.setRootVisible(false);
        JXTree otherTree = new JXTree(treeTableModel);
        otherTree.setRootVisible(true);
        otherTree.setShowsRootHandles(false);
        JFrame frame = wrapWithScrollingInFrame(tree, otherTree, "ShowsRootHandles");
        frame.setVisible(true);  // RG: Changed from deprecated method show();
    }

    protected void setUp() throws Exception {
        super.setUp();
        treeTableModel = new FileSystemModel();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        LFSwitcher.windowsLF();
        } catch (Exception e1) { // ignore
        }
        JXTreeUnitTest test = new JXTreeUnitTest();
        try {
          //  test.runInteractiveTests();
            test.runInteractiveTests("interactive.*High.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
}
