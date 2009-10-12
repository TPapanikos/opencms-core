/*
 * File   : $Source: /alkacon/cvs/opencms/src-setup/org/opencms/setup/xml/CmsXmlRemovePageSearchIndexSource1.java,v $
 * Date   : $Date: 2009/10/12 08:11:54 $
 * Version: $Revision: 1.3.2.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2009 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.setup.xml;

import org.opencms.configuration.CmsConfigurationManager;
import org.opencms.configuration.CmsSearchConfiguration;
import org.opencms.configuration.I_CmsXmlConfiguration;

import java.util.Collections;
import java.util.List;

/**
 * Removes the doctype 'page' from the search index source 'source1'.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.3.2.1 $ 
 * 
 * @since 6.1.8 
 */
public class CmsXmlRemovePageSearchIndexSource1 extends A_CmsSetupXmlUpdate {

    /** List of xpaths to remove. */
    private List<String> m_xpaths;

    /**
     * @see org.opencms.setup.xml.I_CmsSetupXmlUpdate#getName()
     */
    public String getName() {

        return "Remove document type 'page' from the search index source 'source1'";
    }

    /**
     * @see org.opencms.setup.xml.I_CmsSetupXmlUpdate#getXmlFilename()
     */
    public String getXmlFilename() {

        return CmsSearchConfiguration.DEFAULT_XML_FILE_NAME;
    }

    /**
     * @see org.opencms.setup.xml.A_CmsSetupXmlUpdate#getXPathsToRemove()
     */
    @Override
    protected List<String> getXPathsToRemove() {

        if (m_xpaths == null) {
            // /opencms/search/indexsources/indexsource[name='source1']/documenttypes-indexed/name[text()='page']
            StringBuffer xp = new StringBuffer(256);
            xp.append("/");
            xp.append(CmsConfigurationManager.N_ROOT);
            xp.append("/");
            xp.append(CmsSearchConfiguration.N_SEARCH);
            xp.append("/");
            xp.append(CmsSearchConfiguration.N_INDEXSOURCES);
            xp.append("/");
            xp.append(CmsSearchConfiguration.N_INDEXSOURCE);
            xp.append("[");
            xp.append(I_CmsXmlConfiguration.N_NAME);
            xp.append("='source1']/");
            xp.append(CmsSearchConfiguration.N_DOCUMENTTYPES_INDEXED);
            xp.append("/");
            xp.append(I_CmsXmlConfiguration.N_NAME);
            xp.append("[text()='page']");
            m_xpaths = Collections.singletonList(xp.toString());
        }
        return m_xpaths;
    }
}