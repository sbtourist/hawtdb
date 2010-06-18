/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.hawtdb.api;

import java.util.Arrays;
import java.util.List;

/**
 * Implements commonly used Predicates like AND, OR, <, > etc. etc. 
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
final public class Predicates {
    
    /**
     * Implements a logical OR predicate over a list of predicate expressions.
     *
     * @param <Key>
     * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
     */
    static class OrPredicate<Key> implements Predicate<Key> {
        private final List<Predicate<Key>> conditions;

        public OrPredicate(List<Predicate<Key>> conditions) {
            this.conditions = conditions;
        }

        final public boolean isInterestedInKeysBetween(Key first, Key second) {
            for (Predicate<Key> condition : conditions) {
                if( condition.isInterestedInKeysBetween(first, second) ) {
                    return true;
                }
            }
            return false;
        }

        final public boolean isInterestedInKey(Key key) {
            for (Predicate<Key> condition : conditions) {
                if( condition.isInterestedInKey(key) ) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            boolean first=true;
            for (Predicate<Key> condition : conditions) {
                if( !first ) {
                    sb.append(" OR ");
                }
                first=false;
                sb.append("(");
                sb.append(condition);
                sb.append(")");
            }
            return sb.toString();
        }
    }

    /**
     * Implements a logical AND predicate over a list of predicate expressions.
     *
     * @param <Key>
     * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
     */
    static class AndPredicate<Key> implements Predicate<Key> {
        private final List<Predicate<Key>> conditions;

        public AndPredicate(List<Predicate<Key>> conditions) {
            this.conditions = conditions;
        }

        final public boolean isInterestedInKeysBetween(Key first, Key second) {
            for (Predicate<Key> condition : conditions) {
                if( !condition.isInterestedInKeysBetween(first, second) ) {
                    return false;
                }
            }
            return true;
        }

        final public boolean isInterestedInKey(Key key) {
            for (Predicate<Key> condition : conditions) {
                if( !condition.isInterestedInKey(key) ) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            boolean first=true;
            for (Predicate<Key> condition : conditions) {
                if( !first ) {
                    sb.append(" AND ");
                }
                first=false;
                sb.append("(");
                sb.append(condition);
                sb.append(")");
            }
            return sb.toString();
        }
    }

    /**
     * Implements a BETWEEN predicate between two key values.  It matches inclusive on
     * the first value and exclusive on the last value.  The predicate expression is
     * equivalent to: <code>(first <= x) AND (x < last)</code>
     *
     * @param <Key> a Comparable class
     * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
     */
    @SuppressWarnings({"unchecked"})
    static class BetweenPredicate<Key extends Comparable> implements Predicate<Key> {
        private final Key first;
        private final Key last;

        public BetweenPredicate(Key first, Key last) {
            this.first = first;
            this.last = last;
        }

        final public boolean isInterestedInKeysBetween(Key left, Key right) {
            return (right==null || right.compareTo(first)>=0)
                    && (left==null || left.compareTo(last)<0);
        }

        final public boolean isInterestedInKey(Key key) {
            return key.compareTo(first) >=0 && key.compareTo(last) <0;
        }

        @Override
        public String toString() {
            return first+" <= key < "+last;
        }
    }

    /**
     * Implements a greater than predicate.  The predicate expression is
     * equivalent to: <code>x > value</code>
     *
     * @param <Key> a Comparable class
     * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
     */
    @SuppressWarnings({"unchecked"})
    static class GTPredicate<Key extends Comparable> implements Predicate<Key> {
        final private Key value;

        public GTPredicate(Key value) {
            this.value = value;
        }

        final public boolean isInterestedInKeysBetween(Key first, Key second) {
            return second==null || isInterestedInKey(second);
        }

        final public boolean isInterestedInKey(Key key) {
            return key.compareTo(value)>0;
        }

        @Override
        public String toString() {
            return "key > "+ value;
        }
    }

    /**
     * Implements a greater than or equal to predicate.  The predicate expression is
     * equivalent to: <code>x >= value</code>
     *
     * @param <Key> a Comparable class
     * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
     */
    @SuppressWarnings({"unchecked"})
    static class GTEPredicate<Key extends Comparable> implements Predicate<Key> {
        final private Key value;

        public GTEPredicate(Key value) {
            this.value = value;
        }

        final public boolean isInterestedInKeysBetween(Key first, Key second) {
            return second==null || isInterestedInKey(second);
        }

        final public boolean isInterestedInKey(Key key) {
            return key.compareTo(value)>=0;
        }

        @Override
        public String toString() {
            return "key >= "+ value;
        }
    }

    /**
     * Implements a less than predicate.  The predicate expression is
     * equivalent to: <code>x < value</code>
     *
     * @param <Key> a Comparable class
     * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
     */
    @SuppressWarnings({"unchecked"})
    static class LTPredicate<Key extends Comparable> implements Predicate<Key> {
        final private Key value;

        public LTPredicate(Key value) {
            this.value = value;
        }

        final public boolean isInterestedInKeysBetween(Key first, Key second) {
            return first==null || isInterestedInKey(first);
        }

        final public boolean isInterestedInKey(Key key) {
            return key.compareTo(value)<0;
        }

        @Override
        public String toString() {
            return "key < "+ value;
        }
    }

    /**
     * Implements a less than or equal to predicate.  The predicate expression is
     * equivalent to: <code>x <= value</code>.
     *
     * @param <Key> a Comparable class
     * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
     */
    @SuppressWarnings({"unchecked"})
    static class LTEPredicate<Key extends Comparable> implements Predicate<Key> {
        final private Key value;

        public LTEPredicate(Key value) {
            this.value = value;
        }

        final public boolean isInterestedInKeysBetween(Key first, Key second) {
            return first==null || isInterestedInKey(first);
        }

        final public boolean isInterestedInKey(Key key) {
            return key.compareTo(value)<=0;
        }

        @Override
        public String toString() {
            return "key <= "+ value;
        }
    }

    /**
     * Implements a predicate that matches all entries.
     *
     * @param <Key> a Comparable class
     * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
     */
    final static class AllPredicate<Key> implements Predicate<Key> {
        public boolean isInterestedInKeysBetween(Key first, Key second) {
            return true;
        }
        public boolean isInterestedInKey(Key key) {
            return true;
        }
        @Override
        public String toString() {
            return "all";
        }
    }

    /**
     * Implements a predicate that matches no entries.
     *
     * @param <Key> a Comparable class
     * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
     */
    final static class NonePredicate<Key> implements Predicate<Key> {
        public boolean isInterestedInKeysBetween(Key first, Key second) {
            return false;
        }
        public boolean isInterestedInKey(Key key) {
            return false;
        }
        @Override
        public String toString() {
            return "none";
        }
    }

    //
    // Helper static methods to help create predicate expressions.
    //
    public static <Key> Predicate<Key> none() {
        return new NonePredicate<Key>();
    }
    public static <Key> Predicate<Key> all() {
        return new AllPredicate<Key>();
    }

    public static <Key> Predicate<Key> or(Predicate<Key>... conditions) {
        return new OrPredicate<Key>(Arrays.asList(conditions));
    }

    public static <Key> Predicate<Key> or(List<Predicate<Key>> conditions) {
        return new OrPredicate<Key>(conditions);
    }

    public static <Key> Predicate<Key> and(Predicate<Key>... conditions) {
        return new AndPredicate<Key>(Arrays.asList(conditions));
    }

    public static <Key> Predicate<Key> and(List<Predicate<Key>> conditions) {
        return new AndPredicate<Key>(conditions);
    }

    public static <Key extends Comparable> Predicate<Key> gt(Key key) {
        return new GTPredicate<Key>(key);
    }
    public static <Key extends Comparable> Predicate<Key> gte(Key key) {
        return new GTEPredicate<Key>(key);
    }

    public static <Key extends Comparable> Predicate<Key> lt(Key key) {
        return new LTPredicate<Key>(key);
    }
    public static <Key extends Comparable> Predicate<Key> lte(Key key) {
        return new LTEPredicate<Key>(key);
    }

    public static <Key extends Comparable> Predicate<Key> lte(Key first, Key last) {
        return new BetweenPredicate<Key>(first, last);
    }

    /**
     * Uses a predicates to select the keys that will be visited.
     *
     * @param <Key>
     * @param <Value>
     */
    static class PredicateVisitor<Key, Value> implements IndexVisitor<Key, Value> {

        public static final int UNLIMITED=-1;

        private final Predicate<Key> predicate;
        private int limit;

        public PredicateVisitor(Predicate<Key> predicate) {
            this(predicate, UNLIMITED);
        }

        public PredicateVisitor(Predicate<Key> predicate, int limit) {
            this.predicate = predicate;
            this.limit = limit;
        }

        final public void visit(List<Key> keys, List<Value> values) {
            for( int i=0; i < keys.size() && !isSatiated(); i++) {
                Key key = keys.get(i);
                if( predicate.isInterestedInKey(key) ) {
                    if(limit > 0 )
                        limit--;
                    matched(key, values.get(i));
                }
            }
        }

        public boolean isInterestedInKeysBetween(Key first, Key second) {
            return predicate.isInterestedInKeysBetween(first, second);
        }

        public boolean isSatiated() {
            return limit==0;
        }

        /**
         * Subclasses should override.  This method will be called for each key,
         * value pair that matches the predicate.
         *
         * @param key
         * @param value
         */
        protected void matched(Key key, Value value) {
        }

    }

    public static <Key, Value> IndexVisitor<Key, Value> visitor(Predicate<Key> predicate) {
        return new PredicateVisitor<Key, Value>(predicate);
    }

}