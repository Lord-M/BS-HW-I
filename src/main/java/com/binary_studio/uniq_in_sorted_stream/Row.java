package com.binary_studio.uniq_in_sorted_stream;

//You CAN modify this class
public final class Row<RowData> {

	private final Long id;

	public Row(Long id) {
		this.id = id;
	}

	public Long getPrimaryId() {
		return this.id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Row<RowData> row = (Row<RowData>) o;
		return this.id.equals(row.id);
	}

	@Override
	public int hashCode() {
		return Long.hashCode(this.id);
	}

}
