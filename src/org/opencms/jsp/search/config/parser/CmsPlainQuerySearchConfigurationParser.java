/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH & Co. KG (http://www.alkacon.com)
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
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.jsp.search.config.parser;

import org.opencms.jsp.search.config.CmsSearchConfigurationCommon;
import org.opencms.jsp.search.config.I_CmsSearchConfiguration;
import org.opencms.jsp.search.config.I_CmsSearchConfigurationCommon;
import org.opencms.jsp.search.config.I_CmsSearchConfigurationDidYouMean;
import org.opencms.jsp.search.config.I_CmsSearchConfigurationFacetField;
import org.opencms.jsp.search.config.I_CmsSearchConfigurationFacetQuery;
import org.opencms.jsp.search.config.I_CmsSearchConfigurationFacetRange;
import org.opencms.jsp.search.config.I_CmsSearchConfigurationHighlighting;
import org.opencms.jsp.search.config.I_CmsSearchConfigurationPagination;
import org.opencms.jsp.search.config.I_CmsSearchConfigurationSorting;
import org.opencms.main.CmsLog;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;

/** Search configuration parser reading a configuration containing a plain Solr query.
 * Only fl might be added additionally. */
public class CmsPlainQuerySearchConfigurationParser implements I_CmsSearchConfigurationParser {

    /** Logger for the class. */
    protected static final Log LOG = CmsLog.getLog(CmsPlainQuerySearchConfigurationParser.class);

    /** The default return fields. */
    private static final String DEFAULT_FL = "id,path";

    /** The whole query string. */
    protected String m_queryString;

    /** The optional base configuration that should be changed by the JSON configuration. */
    private I_CmsSearchConfiguration m_baseConfig;

    /** Constructor taking the JSON as String.
     * @param query The query that is passed to Solr.
     */
    public CmsPlainQuerySearchConfigurationParser(String query) {

        this(query, null);
    }

    /** Constructor taking the JSON as String.
     * @param query The query that is passed to Solr (additional Solr params).
     * @param baseConfig A base configuration that is adjusted by the JSON configuration string.
     */
    public CmsPlainQuerySearchConfigurationParser(String query, I_CmsSearchConfiguration baseConfig) {

        if ((null != query) && !(query.startsWith("fl=") || query.contains("&fl="))) {
            query = query + "&fl=" + DEFAULT_FL;
        }
        m_queryString = query;
        m_baseConfig = baseConfig;

    }

    /**
     * @see org.opencms.jsp.search.config.parser.I_CmsSearchConfigurationParser#parseCommon()
     */
    public I_CmsSearchConfigurationCommon parseCommon() {

        return new CmsSearchConfigurationCommon(
            null,
            null,
            null,
            null,
            Boolean.TRUE,
            Boolean.TRUE,
            null,
            null,
            null,
            m_queryString,
            null,
            null,
            null);
    }

    /**
     * @see org.opencms.jsp.search.config.parser.I_CmsSearchConfigurationParser#parseDidYouMean()
     */
    public I_CmsSearchConfigurationDidYouMean parseDidYouMean() {

        return null != m_baseConfig ? m_baseConfig.getDidYouMeanConfig() : null;
    }

    /**
     * @see org.opencms.jsp.search.config.parser.I_CmsSearchConfigurationParser#parseFieldFacets()
     */
    public Map<String, I_CmsSearchConfigurationFacetField> parseFieldFacets() {

        return null != m_baseConfig ? m_baseConfig.getFieldFacetConfigs() : Collections.emptyMap();
    }

    /**
     * @see org.opencms.jsp.search.config.parser.I_CmsSearchConfigurationParser#parseHighlighter()
     */
    public I_CmsSearchConfigurationHighlighting parseHighlighter() {

        return null != m_baseConfig ? m_baseConfig.getHighlighterConfig() : null;
    }

    /**
     * @see org.opencms.jsp.search.config.parser.I_CmsSearchConfigurationParser#parsePagination()
     */
    public I_CmsSearchConfigurationPagination parsePagination() {

        return null != m_baseConfig ? m_baseConfig.getPaginationConfig() : null;
    }

    /**
     * @see org.opencms.jsp.search.config.parser.I_CmsSearchConfigurationParser#parseQueryFacet()
     */
    public I_CmsSearchConfigurationFacetQuery parseQueryFacet() {

        return null != m_baseConfig ? m_baseConfig.getQueryFacetConfig() : null;
    }

    /**
     * @see org.opencms.jsp.search.config.parser.I_CmsSearchConfigurationParser#parseRangeFacets()
     */
    public Map<String, I_CmsSearchConfigurationFacetRange> parseRangeFacets() {

        return null != m_baseConfig ? m_baseConfig.getRangeFacetConfigs() : Collections.emptyMap();
    }

    /**
     * @see org.opencms.jsp.search.config.parser.I_CmsSearchConfigurationParser#parseSorting()
     */
    public I_CmsSearchConfigurationSorting parseSorting() {

        return null != m_baseConfig ? m_baseConfig.getSortConfig() : null;
    }
}
