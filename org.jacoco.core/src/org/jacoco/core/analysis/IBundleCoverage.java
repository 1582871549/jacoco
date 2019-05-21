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
package org.jacoco.core.analysis;

import java.util.Collection;

/**
 * 包的覆盖数据。包将包的集合分组
 *
 * @see IPackageCoverage
 */
public interface IBundleCoverage extends ICoverageNode {

    /**
     * 返回该包中包含的所有包
     *
     * @return all packages
     */
    Collection<IPackageCoverage> getPackages();

}
