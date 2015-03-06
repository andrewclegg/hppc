/*! #set($TemplateOptions.ignored = ($TemplateOptions.KTypePrimitive)) !*/
package com.carrotsearch.hppc;

import static com.carrotsearch.hppc.TestUtils.*;

import java.util.*;

import org.junit.*;

import com.carrotsearch.hppc.cursors.*;
import com.carrotsearch.hppc.predicates.*;
import com.carrotsearch.hppc.procedures.*;

/**
 * Tests for {@link KTypeVTypeOpenIdentityHashMap}.
 */
/* ! ${TemplateOptions.generatedAnnotation} ! */
public class KTypeVTypeOpenIdentityHashMapTest<KType, VType> extends AbstractKTypeTest<KType>
{
    protected VType value0 = vcast(0);
    protected VType value1 = vcast(1);
    protected VType value2 = vcast(2);
    protected VType value3 = vcast(3);

    /**
     * Per-test fresh initialized instance.
     */
    public KTypeVTypeOpenIdentityHashMap<KType, VType> map = KTypeVTypeOpenIdentityHashMap.newInstance();

    @After
    public void checkEmptySlotsUninitialized()
    {
        if (map != null)
        {
            int occupied = 0;
            for (int i = 0; i < map.keys.length; i++)
            {
                if (map.allocated[i] == false)
                {
                    /*! #if ($TemplateOptions.KTypeGeneric) !*/
                    assertEquals2(Intrinsics.defaultKTypeValue(), map.keys[i]);
                    /*! #end !*/
                    /*! #if ($TemplateOptions.VTypeGeneric) !*/
                    assertEquals2(Intrinsics.defaultVTypeValue(), map.values[i]);
                    /*! #end !*/
                }
                else
                {
                    occupied++;
                }
            }
            assertEquals(occupied, map.assigned);
        }
    }

    /**
     * Convert to target type from an integer used to test stuff. 
     */
    protected VType vcast(int value)
    {
        /*! #if ($TemplateOptions.VTypePrimitive)
            return (VType) value;
            #else !*/ 
            @SuppressWarnings("unchecked")        
            VType v = (VType)(Object) value;
            return v;
        /*! #end !*/
    }

    /**
     * Create a new array of a given type and copy the arguments to this array.
     */
    /* #if ($TemplateOptions.VTypeGeneric) */ @SuppressWarnings("unchecked") /* #end */
    protected VType [] newvArray(VType... elements)
    {
        return elements;
    }

    private void assertSameMap(
        final KTypeVTypeMap<KType, VType> c1,
        final KTypeVTypeMap<KType, VType> c2)
    {
        assertEquals(c1.size(), c2.size());

        c1.forEach(new KTypeVTypeProcedure<KType, VType>()
        {
            public void apply(KType key, VType value)
            {
                assertTrue(c2.containsKey(key));
                assertEquals2(value, c2.get(key));
            }
        });
    }
    
    /* */
    @Test
    public void testCloningConstructor()
    {
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);

        assertSameMap(map, KTypeVTypeOpenIdentityHashMap.from(map));
        assertSameMap(map, new KTypeVTypeOpenIdentityHashMap<KType, VType>(map));
    }

    /* */
    /*! #if ($TemplateOptions.VTypeGeneric) !*/
    @SuppressWarnings("unchecked")
    /*! #end !*/
    @Test
    public void testFromArrays()
    {
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);

        KTypeVTypeOpenIdentityHashMap<KType, VType> map2 = KTypeVTypeOpenIdentityHashMap.from(
            newArray(key1, key2, key3),
            newvArray(value1, value2, value3));

        assertSameMap(map, map2);
    }

    @Test
    public void testGetOrDefault()
    {
        map.put(key2, value2);
        assertTrue(map.containsKey(key2));

        map.put(key1, value1);
        assertEquals2(value1, map.getOrDefault(key1, value3));
        assertEquals2(value3, map.getOrDefault(key3, value3));
        map.remove(key1);
        assertEquals2(value3, map.getOrDefault(key1, value3));

        // Make sure lslot wasn't touched.
        assertEquals2(value2, map.lget());
    }

    /* */
    @Test
    public void testPut()
    {
        map.put(key1, value1);

        assertTrue(map.containsKey(key1));
        assertEquals2(value1, map.lget());
        assertEquals2(value1, map.get(key1));
    }

    /* */
    @Test
    public void testLPut()
    {
        map.put(key1, value2);
        if (map.containsKey(key1))
            map.lset(value3);

        assertTrue(map.containsKey(key1));
        assertEquals2(value3, map.lget());
        assertEquals2(value3, map.get(key1));
    }

    /* */
    @Test
    public void testPutOverExistingKey()
    {
        map.put(key1, value1);
        assertEquals2(value1, map.put(key1, value3));
        assertEquals2(value3, map.get(key1));
    }

    /* */
    @Test
    public void testPutAll()
    {
        map.put(key1, value1);
        map.put(key2, value1);

        KTypeVTypeOpenIdentityHashMap<KType, VType> map2 = 
            new KTypeVTypeOpenIdentityHashMap<KType, VType>();

        map2.put(key2, value2);
        map2.put(key3, value1);

        // One new key (key3).
        assertEquals(1, map.putAll(map2));
        
        // Assert the value under key2 has been replaced.
        assertEquals2(value2, map.get(key2));

        // And key3 has been added.
        assertEquals2(value1, map.get(key3));
        assertEquals(3, map.size());
    }
    
    /* */
    @Test
    public void testPutIfAbsent()
    {
        assertTrue(map.putIfAbsent(key1, value1));
        assertFalse(map.putIfAbsent(key1, value2));
        assertEquals2(value1, map.get(key1));
    }

    /*! #if ($TemplateOptions.VTypePrimitive)
    @Test
    public void testPutOrAdd()
    {
        assertEquals2(value1, map.putOrAdd(key1, value1, value2));
        assertEquals2(value1 + value2, map.putOrAdd(key1, value1, value2));
    }
    #end !*/

    /*! #if ($TemplateOptions.VTypePrimitive)
    @Test
    public void testAddTo()
    {
        assertEquals2(value1, map.addTo(key1, value1));
        assertEquals2(value1 + value2, map.addTo(key1, value2));
    }
    #end !*/

    /* */
    @Test
    public void testRemove()
    {
        map.put(key1, value1);
        assertEquals2(value1, map.remove(key1));
        assertEquals2(Intrinsics.defaultVTypeValue(), map.remove(key1));
        assertEquals(0, map.size());

        // These are internals, but perhaps worth asserting too.
        assertEquals(0, map.assigned);
    }

    /* */
    @Test
    public void testRemoveAllWithContainer()
    {
        map.put(key1, value1);
        map.put(key2, value1);
        map.put(key3, value1);

        KTypeArrayList<KType> list2 = KTypeArrayList.newInstance();
        list2.add(newArray(key2, key3, key4));

        map.removeAll(list2);
        assertEquals(1, map.size());
        assertTrue(map.containsKey(key1));
    }

    /* */
    @Test
    public void testRemoveAllWithPredicate()
    {
        map.put(key1, value1);
        map.put(key2, value1);
        map.put(key3, value1);

        map.removeAll(new KTypePredicate<KType>()
        {
            public boolean apply(KType value)
            {
                return value == key2 || value == key3;
            }
        });
        assertEquals(1, map.size());
        assertTrue(map.containsKey(key1));
    }

    /* */
    @Test
    public void testRemoveViaKeySetView()
    {
        map.put(key1, value1);
        map.put(key2, value1);
        map.put(key3, value1);

        map.keys().removeAll(new KTypePredicate<KType>()
        {
            public boolean apply(KType value)
            {
                return value == key2 || value == key3;
            }
        });
        assertEquals(1, map.size());
        assertTrue(map.containsKey(key1));
    }

    /* */
    @Test
    public void testMapsIntersection()
    {
        KTypeVTypeOpenIdentityHashMap<KType, VType> map2 = 
            KTypeVTypeOpenIdentityHashMap.newInstance(); 

        map.put(key1, value1);
        map.put(key2, value1);
        map.put(key3, value1);
        
        map2.put(key2, value1);
        map2.put(key4, value1);

        assertEquals(2, map.keys().retainAll(map2.keys()));

        assertEquals(1, map.size());
        assertTrue(map.containsKey(key2));
    }

    /* */
    @Test
    public void testMapKeySet()
    {
        map.put(key1, value3);
        map.put(key2, value2);
        map.put(key3, value1);

        assertSortedListEquals(map.keys().toArray(), key1, key2, key3);
    }

    /* */
    @Test
    public void testMapKeySetIterator()
    {
        map.put(key1, value3);
        map.put(key2, value2);
        map.put(key3, value1);

        int counted = 0;
        for (KTypeCursor<KType> c : map.keys())
        {
            assertEquals2(map.keys[c.index], c.value);
            counted++;
        }
        assertEquals(counted, map.size());
    }

    /* */
    @Test
    public void testClear()
    {
        map.put(key1, value1);
        map.put(key2, value1);
        map.clear();
        assertEquals(0, map.size());

        // These are internals, but perhaps worth asserting too.
        assertEquals(0, map.assigned);
    }

    /* */
    @Test
    public void testRoundCapacity()
    {
        assertEquals(0x40000000, HashContainerUtils.roundCapacity(Integer.MAX_VALUE));
        assertEquals(0x40000000, HashContainerUtils.roundCapacity(Integer.MAX_VALUE / 2 + 1));
        assertEquals(0x40000000, HashContainerUtils.roundCapacity(Integer.MAX_VALUE / 2));
        assertEquals(KTypeVTypeOpenIdentityHashMap.MIN_CAPACITY, HashContainerUtils.roundCapacity(0));
        assertEquals(Math.max(4, KTypeVTypeOpenIdentityHashMap.MIN_CAPACITY), HashContainerUtils.roundCapacity(3));
    }

    /* */
    @Test
    public void testIterable()
    {
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.remove(key2);

        int count = 0;
        for (KTypeVTypeCursor<KType, VType> cursor : map)
        {
            count++;
            assertTrue(map.containsKey(cursor.key));
            assertEquals2(cursor.value, map.get(cursor.key));

            assertEquals2(cursor.value, map.values[cursor.index]);
            assertEquals2(cursor.key, map.keys[cursor.index]);
            assertEquals2(true, map.allocated[cursor.index]);
        }
        assertEquals(count, map.size());

        map.clear();
        assertFalse(map.iterator().hasNext());
    }

    /* */
    @Test
    public void testFullLoadFactor()
    {
        map = new KTypeVTypeOpenIdentityHashMap<KType, VType>(1, 1f);

        // Fit in the byte key range.
        int capacity = 0x80;
        int max = capacity - 1;
        for (int i = 0; i < max; i++)
        {
            map.put(cast(i), value1);
        }

        // Still not expanded.
        assertEquals(max, map.size());
        assertEquals(capacity, map.keys.length);
        // Won't expand (existing key).
        map.put(cast(0), value2);
        assertEquals(capacity, map.keys.length);
        // Expanded.
        map.put(cast(0xff), value2);
        assertEquals(2 * capacity, map.keys.length);
    }


    /* */
    @Test
    public void testBug_HPPC73_FullCapacityGet()
    {
        map = new KTypeVTypeOpenIdentityHashMap<KType, VType>(1, 1f);
        int capacity = 0x80;
        int max = capacity - 1;
        for (int i = 0; i < max; i++)
        {
            map.put(cast(i), value1);
        }
        assertEquals(max, map.size());
        assertEquals(capacity, map.keys.length);
        
        // Non-existent key.
        map.remove(cast(max + 1));
        assertFalse(map.containsKey(cast(max + 1)));
        assertEquals2(Intrinsics.defaultVTypeValue(), map.get(cast(max + 1)));

        // Should not expand because we're replacing an existing element.
        map.put(cast(0), value2);
        assertEquals(max, map.size());
        assertEquals(capacity, map.keys.length);

        map.putIfAbsent(cast(0), value3);
        assertEquals(max, map.size());
        assertEquals(capacity, map.keys.length);

        // Remove from a full map.
        map.remove(cast(0));
        assertEquals(max - 1, map.size());
        assertEquals(capacity, map.keys.length);
        
        // Check expand on "last slot of a full map" condition.
        map.put(cast(0), value1);
        map.put(cast(max), value1);
        assertEquals(max + 1, map.size());
        assertEquals(capacity << 1, map.keys.length);
    }

    /* */
    @Test
    public void testHalfLoadFactor()
    {
        map = new KTypeVTypeOpenIdentityHashMap<KType, VType>(1, 0.5f);

        int capacity = 0x80;
        int max = capacity - 1;
        for (int i = 0; i < max; i++)
        {
            map.put(cast(i), value1);
        }

        assertEquals(max, map.size());
        // Still not expanded.
        assertEquals(2 * capacity, map.keys.length);
        // Won't expand (existing key);
        map.put(cast(0), value2);
        assertEquals(2 * capacity, map.keys.length);
        // Expanded.
        map.put(cast(0xff), value1);
        assertEquals(4 * capacity, map.keys.length);
    }

    /*! #if ($TemplateOptions.VTypeGeneric) !*/
    @SuppressWarnings("unchecked")
    /*! #end !*/
    @Test
    public void testHashCodeEquals()
    {
        KTypeVTypeOpenIdentityHashMap<KType, VType> l0 = 
            new KTypeVTypeOpenIdentityHashMap<KType, VType>();
        assertEquals(0, l0.hashCode());
        assertEquals(l0, new KTypeVTypeOpenIdentityHashMap<KType, VType>());

        KTypeVTypeOpenIdentityHashMap<KType, VType> l1 = KTypeVTypeOpenIdentityHashMap.from(
            newArray(key1, key2, key3),
            newvArray(value1, value2, value3));

        KTypeVTypeOpenIdentityHashMap<KType, VType> l2 = KTypeVTypeOpenIdentityHashMap.from(
            newArray(key2, key1, key3),
            newvArray(value2, value1, value3));

        KTypeVTypeOpenIdentityHashMap<KType, VType> l3 = KTypeVTypeOpenIdentityHashMap.from(
            newArray(key1, key2),
            newvArray(value2, value1));

        assertEquals(l1.hashCode(), l2.hashCode());
        assertEquals(l1, l2);

        assertFalse(l1.equals(l3));
        assertFalse(l2.equals(l3));
    }

    /*! #if ($TemplateOptions.VTypeGeneric) !*/
    @SuppressWarnings("unchecked")
    /*! #end !*/
    @Test
    public void testBug_HPPC37()
    {
        KTypeVTypeOpenIdentityHashMap<KType, VType> l1 = KTypeVTypeOpenIdentityHashMap.from(
            newArray(key1),
            newvArray(value1));

        KTypeVTypeOpenIdentityHashMap<KType, VType> l2 = KTypeVTypeOpenIdentityHashMap.from(
            newArray(key2),
            newvArray(value1));

        assertFalse(l1.equals(l2));
        assertFalse(l2.equals(l1));
    }    

    /*! #if ($TemplateOptions.KTypeGeneric) !*/
    @Test
    public void testNullKey()
    {
        map.put(null, vcast(10));
        assertEquals2(vcast(10), map.get(null));
        assertTrue(map.containsKey(null));
        assertEquals2(vcast(10), map.lget());
        assertEquals2(null, map.lkey());
        map.remove(null);
        assertEquals(0, map.size());
    }
    /*! #end !*/

    /*! #if ($TemplateOptions.KTypeGeneric) !*/
    @Test
    @SuppressWarnings("unchecked")
    public void testLkey()
    {
        map.put(key1, vcast(10));
        assertTrue(map.containsKey(key1));
        assertSame(key1, map.lkey());
        KType key1_ = (KType) new Integer(1);
        assertNotSame(key1, key1_);
        assertEquals(key1, key1_);
        assertFalse(map.containsKey(key1_));
    }
    /*! #end !*/

    /*! #if ($TemplateOptions.VTypeGeneric) !*/
    @Test
    public void testNullValue()
    {
        map.put(key1, null);
        assertEquals(null, map.get(key1));
        assertTrue(map.containsKey(key1));
        map.remove(key1);
        assertFalse(map.containsKey(key1));
        assertEquals(0, map.size());
    }
    /*! #end !*/

    /*! #if ($TemplateOptions.AllGeneric) !*/
    /**
     * Run some random insertions/ deletions and compare the results
     * against <code>java.util.IdentityHashMap</code>.
     */
    @Test
    public void testAgainstIdentityHashMap()
    {
        final Random rnd = new Random(randomLong());
        final IdentityHashMap<KType, VType> other = 
            new IdentityHashMap<KType, VType>();

        for (int size = 1000; size < 20000; size += 4000)
        {
            other.clear();
            map.clear();

            for (int round = 0; round < size * 20; round++)
            {
                KType key = cast(rnd.nextInt(size));
                VType value = vcast(rnd.nextInt());

                if (rnd.nextBoolean())
                {
                    map.put(key, value);
                    other.put(key, value);

                    assertEquals(value, map.get(key));
                    assertTrue(map.containsKey(key));
                    assertEquals(value, map.lget());
                }
                else
                {
                    assertEquals(other.remove(key), map.remove(key));
                }

                assertEquals(other.size(), map.size());
            }
        }
    }
    /*! #end !*/
    
    /*
     * 
     */
    @Test
    public void testClone()
    {
        this.map.put(key1, value1);
        this.map.put(key2, value2);
        this.map.put(key3, value3);

        KTypeVTypeOpenIdentityHashMap<KType, VType> cloned = map.clone();
        cloned.remove(key1);

        assertSortedListEquals(map.keys().toArray(), key1, key2, key3);
        assertSortedListEquals(cloned.keys().toArray(), key2, key3);
    }

    /*
     * 
     */
    @Test
    public void testToString()
    {
        Assume.assumeTrue(
            (int[].class.isInstance(map.keys)     ||
             short[].class.isInstance(map.keys)   ||
             byte[].class.isInstance(map.keys)    ||
             long[].class.isInstance(map.keys)    ||
             Object[].class.isInstance(map.keys)) &&
            (int[].class.isInstance(map.values)   ||
             byte[].class.isInstance(map.values)  ||
             short[].class.isInstance(map.values) ||
             long[].class.isInstance(map.values)  ||
             Object[].class.isInstance(map.values)));

        this.map.put(key1, value1);
        this.map.put(key2, value2);

        String asString = map.toString();
        asString = asString.replaceAll("[^0-9]", "");
        char [] asCharArray = asString.toCharArray();
        Arrays.sort(asCharArray);
        assertEquals("1122", new String(asCharArray));
    }

    /* */
    @Test
    public void testMapValues()
    {
        map.put(key1, value3);
        map.put(key2, value2);
        map.put(key3, value1);
        assertSortedListEquals(map.values().toArray(), value1, value2, value3);

        map.clear();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value2);
        assertSortedListEquals(map.values().toArray(), value1, value2, value2);        
    }

    /* */
    @Test
    public void testMapValuesIterator()
    {
        map.put(key1, value3);
        map.put(key2, value2);
        map.put(key3, value1);

        int counted = 0;
        for (KTypeCursor<VType> c : map.values())
        {
            assertEquals2(map.values[c.index], c.value);
            counted++;
        }
        assertEquals(counted, map.size());
    }

    /* */
    @Test
    public void testMapValuesContainer()
    {
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value2);

        // contains()
        for (KTypeVTypeCursor<KType, VType> c : map)
            assertTrue(map.values().contains(c.value));
        assertFalse(map.values().contains(value3));
        
        assertEquals(map.isEmpty(), map.values().isEmpty());
        assertEquals(map.size(), map.values().size());

        final KTypeArrayList<VType> values = new KTypeArrayList<VType>();
        map.values().forEach(new KTypeProcedure<VType>()
            {
                public void apply(VType value)
                {
                    values.add(value);
                }
            });
        assertSortedListEquals(map.values().toArray(), value1, value2, value2);

        values.clear();
        map.values().forEach(new KTypePredicate<VType>()
            {
                public boolean apply(VType value)
                {
                    values.add(value);
                    return true;
                }
            });
        assertSortedListEquals(map.values().toArray(), value1, value2, value2);
    }

    /**
     * Tests that instances created with the <code>newInstanceWithExpectedSize</code>
     * static factory methods do not have to resize to hold the expected number of elements.
     */
    @Test
    public void testExpectedSizeInstanceCreation()
    {
        KTypeVTypeOpenIdentityHashMap<KType, VType> fixture =
                KTypeVTypeOpenIdentityHashMap.newInstanceWithExpectedSize(KTypeVTypeOpenIdentityHashMap.DEFAULT_CAPACITY);

        assertEquals(KTypeVTypeOpenIdentityHashMap.DEFAULT_CAPACITY, this.map.keys.length);
        assertEquals(KTypeVTypeOpenIdentityHashMap.DEFAULT_CAPACITY * 2, fixture.keys.length);

        for (int i = 0; i < KTypeOpenHashSet.DEFAULT_CAPACITY; i++)
        {
            KType key = cast(i);
            VType value = vcast(i);
            this.map.put(key, value);
            fixture.put(key, value);
        }

        assertEquals(KTypeVTypeOpenIdentityHashMap.DEFAULT_CAPACITY * 2, this.map.keys.length);
        assertEquals(KTypeVTypeOpenIdentityHashMap.DEFAULT_CAPACITY * 2, fixture.keys.length);
    }
}
