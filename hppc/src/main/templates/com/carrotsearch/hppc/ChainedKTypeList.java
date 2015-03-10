package com.carrotsearch.hppc; // because Internals is package-private :-(

import java.util.*;

import com.carrotsearch.hppc.cursors.*;
import com.carrotsearch.hppc.predicates.KTypePredicate;
import com.carrotsearch.hppc.procedures.*;

import static com.carrotsearch.hppc.Internals.*;

/**
 * A multi-array-backed list of KTypes. A chain of arrays is used to store and manipulate
 * all elements. Reallocations are governed by a {@link ArraySizingStrategy}
 * and may be expensive if they move around really large chunks of memory.
 */
/*! ${TemplateOptions.generatedAnnotation} !*/
public class ChainedKTypeList<KType>
    extends AbstractKTypeCollection<KType> implements KTypeIndexedContainer<KType>, Cloneable
{


    /**
     * Internal static instance of an empty buffer.
     */
    private final static Object EMPTY = /*! 
            #if ($TemplateOptions.KTypePrimitive) 
              new KType [0][0];
            #else !*/
              new Object [0][0];
        /*! #end !*/


    /**
     * Internal arrays for storing the list.
     * 
#if ($TemplateOptions.KTypeGeneric) 
     * <p><strong>Important!</strong> 
     * The actual value in this field is always an instance of <code>Object[][]</code>,
     * regardless of the generic type used. The JDK is inconsistent here too:
     * {@link ArrayList} declares internal <code>Object[]</code> buffer, but
     * {@link ArrayDeque} declares an array of generic type objects like we do. The
     * tradeoff is probably minimal, but you should be aware of additional casts generated
     * by <code>javac</code> when <code>buffer</code> is directly accessed; <strong>these casts
     * may also result in exceptions at runtime</strong>. A workaround is to cast directly to
     * <code>Object[][]</code> before accessing the buffer's elements, as shown
     * in the following code snippet.</p>
     * 
     * <pre>
     * Object[][] buf = list.buffers;
     * for (int i = list.size(); --i >= 0;) {
     *   doSomething(buf[i]);
     * }
     * </pre>
#end
     */
    public KType [][] buffers;

    public int blockLen;

    public int elementsCount;

	// For now
	private final String lengthChangeError = this.getClass().getSimpleName() +
			" is fixed length, and does not support operations that would shorten or lengthen it";

    /**
     * Default constructor just creates an empty list with no spare capacity.
     */
    public ChainedKTypeList() {
        release();
    }


    /**
     * Replace the current backing storage with totally new data, updating the
     * appropriate counters automatically, and potentially freeing up the old
     * data for GCing.
     */
    public void setBuffers(KType[][] newBuffers) {
        // Check they're all the same size, except potentially the last one
        int totalLen = 0;
        for (int i = 0; i < newBuffers.length - 1; i++) {
            final int currLen = newBuffers[i].length;
            assert currLen == newBuffers[i + 1].length;
            totalLen += currLen;
        }
        buffers = newBuffers;
        blockLen = newBuffers[0].length;
        elementsCount = totalLen;
    }

    // TODO put resizing strategy back in when we need it

    // TODO other useful constructors e.g. copy constructor

    // TODO release method

    /**
     * Sets the number of stored elements to zero and releases the internal storage arrays.
     */
    /* #if ($TemplateOptions.KTypeGeneric) */
    @SuppressWarnings("unchecked") 
    /* #end */
    public void release()
    {
        buffers = (KType[][]) EMPTY;
        blockLen = 0;
        elementsCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(KType e1)
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(int index, KType e1)
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KType get(int index)
    {
        assert (index >= 0 && index < size()) :
            "Index " + index + " out of bounds [" + 0 + ", " + size() + ").";

		final int block = index / blockLen;
		final int offset = index % blockLen;
		return buffers[block][offset];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KType set(int index, KType e1)
    {
        assert (index >= 0 && index < size()) :
            "Index " + index + " out of bounds [" + 0 + ", " + size() + ").";

		final int block = index / blockLen;
		final int offset = index % blockLen;
		final KType v = buffers[block][offset];
		buffers[block][offset] = e1;
		return v;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public KType remove(int index)
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRange(int fromIndex, int toIndex)
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int removeFirstOccurrence(KType e1)
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public int removeLastOccurrence(KType e1)
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int removeAllOccurrences(KType e1)
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(KType e1)
    {
        return indexOf(e1) >= 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(KType e1)
    {
		for (int i = 0; i < size(); i++) {
			if (Intrinsics.equalsKType(e1, get(i)))
				return i;
		}
		return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastIndexOf(KType e1)
    {
		for (int i = size() - 1; i >= 0; i--) {
			if (Intrinsics.equalsKType(e1, get(i)))
				return i;
		}
		return -1;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isEmpty()
    {
        return elementsCount == 0;
    }

    /**
     * Increases the capacity of this instance, if necessary, to ensure 
     * that it can hold at least the number of elements specified by 
     * the minimum capacity argument.
     */
    public void ensureCapacity(int minCapacity) 
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * Ensures the internal buffer has enough free slots to store
     * <code>expectedAdditions</code>. Increases internal buffer size if needed.
     */
    protected void ensureBufferSpace(int expectedAdditions)
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * Truncate or expand the list to the new size. If the list is truncated, the buffer
     * will not be reallocated (use {@link #trimToSize()} if you need a truncated buffer),
     * but the truncated values will be reset to the default value (zero). If the list is
     * expanded, the elements beyond the current size are initialized with JVM-defaults
     * (zero or <code>null</code> values).
     */
    public void resize(int newSize)
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size()
    {
        return elementsCount;
    }

    /**
     * Trim the internal buffer to the current size.
     */
    /* #if ($TemplateOptions.KTypeGeneric) */
    @SuppressWarnings("unchecked")
    /* #end */
    public void trimToSize()
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * Sets the number of stored elements to zero. Releases and initializes the
     * internal storage array to default values. To clear the list without cleaning
     * the buffer, simply set the {@link #elementsCount} field to zero.
     */
    @Override
    public void clear()
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>The returned array is sized to match exactly
     * the number of elements of the list.</p>
     */
    @Override
    /*! #if ($TemplateOptions.KTypePrimitive)
    public KType [] toArray()
        #else !*/
    public Object [] toArray()
    /*! #end !*/
    {
        /*! #if ($TemplateOptions.KTypePrimitive)
		final KType[] output = new KType[size()];
            #else !*/
        final Object[] output = new Object[size()];
        /*! #end !*/
		for (int i = 0; i < buffers.length; i++) {
			System.arraycopy(buffers[i], 0, output, i * blockLen, buffers[i].length);
		}
		return output;

    }

    /**
     * Clone this object. The returned clone will reuse the same hash function
     * and array resizing strategy. FIXME this needs some thought
     */
    @Override
    public ChainedKTypeList<KType> clone()
    {
        try
        {
            /* #if ($TemplateOptions.KTypeGeneric) */
            @SuppressWarnings("unchecked")
            /* #end */
            final ChainedKTypeList<KType> cloned = (ChainedKTypeList<KType>) super.clone();
            cloned.buffers = buffers.clone();
            return cloned;
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        // Is this definitely the right thing to do?
        return Arrays.deepHashCode(buffers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    /* #if ($TemplateOptions.KTypeGeneric) */
    @SuppressWarnings("unchecked") 
    /* #end */
    public boolean equals(Object obj)
    {
        if (obj != null)
        {
            if (obj instanceof ChainedKTypeList<?>)
            {
                ChainedKTypeList<?> other = (ChainedKTypeList<?>) obj;
                return other.size() == this.size() &&
                    Arrays.deepEquals(this.buffers, other.buffers);
            }
            else if (obj instanceof KTypeIndexedContainer<?>)
            {
                KTypeIndexedContainer<?> other = (KTypeIndexedContainer<?>) obj;
                return other.size() == this.size() &&
                    allIndexesEqual(this, (KTypeIndexedContainer<KType>) other, this.size());
            }
        }
        return false;
    }

    /**
     * Compare a range of values in two arrays. 
     */
    /*! #if ($TemplateOptions.KTypePrimitive) 
    private boolean rangeEquals(KType [] b1, KType [] b2, int length)
        #else !*/
    private boolean rangeEquals(Object [] b1, Object [] b2, int length)
    /*! #end !*/
    {
        for (int i = 0; i < length; i++)
        {
            if (!Intrinsics.equalsKType(b1[i], b2[i]))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Compare index-aligned objects. 
     */
    private boolean allIndexesEqual(
        KTypeIndexedContainer<KType> b1, 
        KTypeIndexedContainer<KType> b2, int length)
    {
        for (int i = 0; i < length; i++)
        {
            KType o1 = b1.get(i); 
            KType o2 = b2.get(i);

            if (!Intrinsics.equalsKType(o1, o2))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * An iterator implementation.
     */
    final static class ValueIterator<KType> extends AbstractIterator<KTypeCursor<KType>>
    {
        private final KTypeCursor<KType> cursor;

        private final ChainedKTypeList<KType> list;
        
        public ValueIterator(ChainedKTypeList<KType> list)
        {
            this.cursor = new KTypeCursor<KType>();
            this.cursor.index = -1;
            this.list = list;
        }

        @Override
        protected KTypeCursor<KType> fetch()
        {
            if (cursor.index + 1 == list.size())
                return done();

            cursor.value = list.get(++cursor.index);
            return cursor;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<KTypeCursor<KType>> iterator()
    {
        return new ValueIterator<KType>(this);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public <T extends KTypeProcedure<? super KType>> T forEach(T procedure)
    {
        return forEach(procedure, 0, size());
    }

    /**
     * Applies <code>procedure</code> to a slice of the list,
     * <code>fromIndex</code>, inclusive, to <code>toIndex</code>, 
     * exclusive.
     */
    public <T extends KTypeProcedure<? super KType>> T forEach(T procedure, 
        int fromIndex, final int toIndex)
    {
        assert (fromIndex >= 0 && fromIndex <= size()) :
            "Index " + fromIndex + " out of bounds [" + 0 + ", " + size() + ").";

        assert (toIndex >= 0 && toIndex <= size()) :
            "Index " + toIndex + " out of bounds [" + 0 + ", " + size() + "].";
        
        assert fromIndex <= toIndex : "fromIndex must be <= toIndex: "
            + fromIndex + ", " + toIndex;

        for (int i = fromIndex; i < toIndex; i++)
        {
            procedure.apply(this.get(i));
        }

        return procedure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int removeAll(KTypePredicate<? super KType> predicate)
    {
		throw new UnsupportedOperationException(lengthChangeError);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public <T extends KTypePredicate<? super KType>> T forEach(T predicate)
    {
        return forEach(predicate, 0, size());
    }

    /**
     * Applies <code>predicate</code> to a slice of the list,
     * <code>fromIndex</code>, inclusive, to <code>toIndex</code>, 
     * exclusive, or until predicate returns <code>false</code>.
     */
    public <T extends KTypePredicate<? super KType>> T forEach(T predicate, 
        int fromIndex, final int toIndex)
    {
        assert (fromIndex >= 0 && fromIndex <= size()) :
            "Index " + fromIndex + " out of bounds [" + 0 + ", " + size() + ").";

        assert (toIndex >= 0 && toIndex <= size()) :
            "Index " + toIndex + " out of bounds [" + 0 + ", " + size() + "].";
        
        assert fromIndex <= toIndex : "fromIndex must be <= toIndex: "
            + fromIndex + ", " + toIndex;

        for (int i = fromIndex; i < toIndex; i++)
        {
            if (!predicate.apply(this.get(i)))
                break;
        }
        
        return predicate;
    }

    /**
     * Returns a new empty instance of this class with no need to declare generic type (shortcut
     * instead of using a constructor).
     */
    public static /*! #if ($TemplateOptions.KTypeGeneric) !*/ <KType> /*! #end !*/
      ChainedKTypeList<KType> newInstance()
    {
        return new ChainedKTypeList<KType>();
    }

    /**
     * Returns a new instance of this class with no need to declare generic type (shortcut
     * instead of using a constructor), using freshly-allocated arrays.
     */
    public static /*! #if ($TemplateOptions.KTypeGeneric) !*/ <KType> /*! #end !*/
      ChainedKTypeList<KType> newInstanceWithCapacity(int ... sizes)
    {
        int n = sizes.length;

        KType[][] outer = newArray(n);

        for (int i = 0; i < n; i++) {
            outer[i] = /*! #if ($TemplateOptions.KTypePrimitive)
                new KType[sizes[i]];
                    #else !*/
                newArray(sizes[i]);
                /*! #end !*/
        }

        ChainedKTypeList<KType> output = new ChainedKTypeList<KType>();
        output.setBuffers(outer);
        return output;
    }


    @SuppressWarnings("unchecked")
    private static <T> T[] newArray(int size) {
        return (T[]) new Object[size];
    }


}
