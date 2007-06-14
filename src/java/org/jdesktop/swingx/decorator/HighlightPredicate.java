/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdesktop.swingx.RolloverProducer;
import org.jdesktop.swingx.util.Contract;

/**
 * The predicate used by AbstractHighlighter to control 
 * highlight on/off.
 * 
 * @author Jeanette Winzenburg
 */
public interface HighlightPredicate {
    
    /**
     * Unconditional true.
     */
    public static final HighlightPredicate ALWAYS = new HighlightPredicate() {

        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return true;
        }
        
    };

    /**
     * Unconditional false.
     */
    public static final HighlightPredicate NEVER = new HighlightPredicate() {

        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return false;
        }
        
    };
    
    /**
     * Rollover
     */
    public static final HighlightPredicate ROLLOVER_ROW = new HighlightPredicate() {
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            // JW: where to put constants? 
            // this is a back-reference into swingx simply to access
            // a string constant. Hmmm...
            Point p = (Point) adapter.getComponent().getClientProperty(
                    RolloverProducer.ROLLOVER_KEY);
            return p != null &&  p.y == adapter.row;
        }
        
    };
    
    /**
     * Even rows.
     * 
     * PENDING: this is zero based (that is "really" even 0, 2, 4 ..), differing 
     * from the old AlternateRowHighlighter.
     * 
     */
    public static final HighlightPredicate EVEN = new HighlightPredicate() {

        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return adapter.row % 2 == 0;
        }
        
    };
    
    /**
     * Odd rows.
     * 
     * PENDING: this is zero based (that is 1, 3, 4 ..), differs from 
     * the old implementation which was one based?
     * 
     */
    public static final HighlightPredicate ODD = new HighlightPredicate() {

        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return !EVEN.isHighlighted(renderer, adapter);
        }
        
    };
    
    
    /**
     * Returns a boolean to indicate whether the component should be 
     * highlighted.
     * 
    * @param renderer the cell renderer component that is to be decorated
    * @param adapter the ComponentAdapter for this decorate operation
    * @return a boolean to indicate whether the component should be highlighted.
     */
    boolean isHighlighted(Component renderer, ComponentAdapter adapter);

    
//----------------- logical implementations amongst HighlightPredicates
    
    /**
     * Negation of a HighlightPredicate.
     */
    public static class NotHighlightPredicate implements HighlightPredicate {
        
        private HighlightPredicate predicate;
        
        /**
         * Instantiates a not against the given predicate.
         * @param predicate the predicate to negate, must not be null.
         * @throws NullPointerException if the predicate is null
         */
        public NotHighlightPredicate(HighlightPredicate predicate) {
            if (predicate == null) 
                throw new NullPointerException("predicate must not be null");
            this.predicate = predicate;
        }
        
        /**
         * @inheritDoc
         * Implemented to return the negation of the given predicate.
         */
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return !predicate.isHighlighted(renderer, adapter);
        }
        
    }
    
    /**
     * Ands a list of predicates.
     */
    public static class AndHighlightPredicate implements HighlightPredicate {
        
        private List<HighlightPredicate> predicate;
        
        /**
         * Instantiates a predicate which ands all given predicates.
         * @param predicate zero or more not null predicates to and
         * @throws NullPointerException if the predicate is null
         */
        public AndHighlightPredicate(HighlightPredicate... predicate) {
            if (predicate == null) 
                throw new NullPointerException("predicate must not be null");
            this.predicate = Arrays.asList(predicate);
        }
        
        /**
         * {@inheritDoc}
         * Implemented to return false if any of the contained predicates is
         * false.
         */
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            for (HighlightPredicate hp : predicate) {
                if (!hp.isHighlighted(renderer, adapter)) return false;
            }
            return true;
        }
        
    }
    
    /**
     * Or's a list of predicates.
     */
    public static class OrHighlightPredicate implements HighlightPredicate {
        
        private List<HighlightPredicate> predicate;
        
        /**
         * Instantiates a predicate which ORs all given predicates.
         * @param predicate zero or more not null predicates to OR
         * @throws NullPointerException if the predicate is null
         */
        public OrHighlightPredicate(HighlightPredicate... predicate) {
//            if (predicate == null) 
//                throw new NullPointerException("predicate must not be null");
            this.predicate = Arrays.asList(Contract.checkNull(predicate, "predicate must not be null"));
        }
        
        /**
         * {@inheritDoc}
         * Implemented to return true if any of the contained predicates is
         * true.
         */
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            for (HighlightPredicate hp : predicate) {
                if (hp.isHighlighted(renderer, adapter)) return true;
            }
            return false;
        }
        
    }
    
//------------------------ coordinates
    
    public static class RowGroupHighlightPredicate implements HighlightPredicate {

        private int linesPerGroup;

        /**
         * Instantiates a predicate with the given grouping.
         * 
         * @param linesPerGroup number of lines constituting a group, must
         *    be > 0
         * @throws IllegalArgumentException if linesPerGroup < 1   
         */
        public RowGroupHighlightPredicate(int linesPerGroup) {
            if (linesPerGroup < 1) 
                throw new IllegalArgumentException("a group contain at least 1 row, was: " + linesPerGroup);
            this.linesPerGroup = linesPerGroup;
        }
        
        /**
         * {@inheritDoc}
         * Implemented to return true if the adapter's row falls into a 
         * odd group number.
         */
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return (adapter.row / linesPerGroup) % 2 == 1;
        }
        
    }
    
    /**
     * A HighlightPredicate based on column index.
     * 
     */
    public static class ColumnHighlightPredicate implements HighlightPredicate {
        List<Integer> columnList;
        
        /**
         * Instantiates a predicate which returns true for the
         * given columns in model coodinates.
         * 
         * @param columns the columns to highlight in model coordinates.
         */
        public ColumnHighlightPredicate(int... columns) {
            columnList = new ArrayList<Integer>();
            for (int i = 0; i < columns.length; i++) {
                columnList.add(columns[i]);
            }
        }
        
        /**
         * {@inheritDoc}
         * 
         * This implementation returns true if the adapter's column
         * is contained in this predicates list.
         * 
         */
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            int modelIndex = adapter.viewToModel(adapter.column);
            return columnList.contains(modelIndex);
        }
        
    }
    
    //--------------------- value testing
    
    /**
     * Predicate testing the componentAdapter value against a fixed
     * Object. 
     */
    public static class EqualsHighlightPredicate implements HighlightPredicate {

        private Object compareValue;
        
        /**
         * Instantitates a predicate with null compare value.
         *
         */
        public EqualsHighlightPredicate() {
            this(null);
        }
        /**
         * Instantitates a predicate with the given compare value.
         * @param compareValue the fixed value to compare the 
         *   adapter against.
         */
        public EqualsHighlightPredicate(Object compareValue) {
            this.compareValue = compareValue;
        }
        
        /**
         * @inheritDoc
         * 
         * Implemented to return true if the adapter value equals the 
         * this predicate's compare value.
         */
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            if (compareValue == null) return adapter.getValue() == null;
            return compareValue.equals(adapter.getValue());
        }
        
    }
}
