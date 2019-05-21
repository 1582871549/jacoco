/*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.core.data;

/**
 * Interface for data output of collected execution data. This interface is
 * meant to be implemented by parties that want to retrieve data from the
 * coverage runtime.
 *
 * 收集的执行数据的数据输出接口。该接口旨在由希望从覆盖运行时检索数据的各方实现。
 */
public interface IExecutionDataVisitor {

	/**
	 * Provides execution data for a class.
     * 提供类的执行数据。
	 * 
	 * @param data
	 *            execution data for a class
	 */
	void visitClassExecution(ExecutionData data);

}
