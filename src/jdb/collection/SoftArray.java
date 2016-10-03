package jdb.collection;

import java.lang.ref.SoftReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SoftArray<T> extends AbstractList<T>{
	private static class ChunkIndex{
		int chunk;
		int index;
		public ChunkIndex(int chunk, int index) {
			this.chunk = chunk;
			this.index = index;
		}
	}
	private List<Integer> dataLength = new ArrayList<>();
	private List<SoftReference<ArrayList<T>>> data = new LinkedList<>();
	private int size = 0;
	
	@Override
	public void add(int index, T element) {
		ChunkIndex chunkIndex=seachChunk(index);
		data.get(chunkIndex.chunk).get().add(chunkIndex.index,element);
		dataLength.set(chunkIndex.chunk, dataLength.get(chunkIndex.chunk)+1);
		size++;
	}
	private ChunkIndex seachChunk(int index) {
		if(size==0) {
			addNewChunk(0);
			return new ChunkIndex(0,0);
		}
		int chunk = 0;
		int shift = 0;
		while(shift+dataLength.get(chunk)<index){
			shift+=dataLength.get(chunk);
			chunk++;
		}
		return new ChunkIndex(chunk,index - shift);
	}
	private ArrayList<T> addNewChunk(int chunkIndex) {
		ArrayList<T> chunk = new ArrayList<T>();
		data.add(chunkIndex, new SoftReference<ArrayList<T>>(chunk));
		dataLength.add(chunkIndex, 0);
		return chunk;
	}
	@Override
	public T remove(int index) {
		ChunkIndex chunkIndex=seachChunk(index);
		size--;
		dataLength.set(chunkIndex.chunk, dataLength.get(chunkIndex.chunk)-1);
		return data.get(chunkIndex.chunk).get().remove(chunkIndex.index);
	}
	@Override
	public T get(int index) {
		ChunkIndex chunkIndex=seachChunk(index);
		//System.out.println(chunkIndex.chunk+":"+chunkIndex.index);
		return data.get(chunkIndex.chunk).get().get(chunkIndex.index);
	}
	@Override
	public int size() {
		return size ;
	}
	
}
