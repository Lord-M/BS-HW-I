package com.binary_studio.tree_max_depth;

import java.util.LinkedList;
import java.util.Queue;

public final class DepartmentMaxDepth {

	private DepartmentMaxDepth() {
	}

	public static Integer calculateMaxDepth(Department rootDepartment) {
		if (rootDepartment == null) {
			return 0;
		}

		int maxDepth = 1;

		Queue<Department> checkList = new LinkedList();
		checkList.add(rootDepartment);

		for (;;) {
			// если все узлы - пустые, или если нет узлов
			var allEmpty = checkList.stream().allMatch(
					d -> d.subDepartments.isEmpty() || d.subDepartments.stream().allMatch(subDep -> subDep == null));
			if (allEmpty) {
				return maxDepth;
			}
			else {
				Queue<Department> newCheckList = new LinkedList();

				// проверить все дочерние узлы из "checkList"
				for (var childNode : checkList) {

					for (int j = 0; j < childNode.subDepartments.size(); j++) {
						var childDept = childNode.subDepartments.get(j);
						if (childDept != null) {
							newCheckList.add(childDept);
						}
					}
				}
				checkList = newCheckList;
				maxDepth++;
			}

		}
	}

}
