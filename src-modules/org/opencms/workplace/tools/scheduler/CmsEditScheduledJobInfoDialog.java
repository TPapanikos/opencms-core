/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/workplace/tools/scheduler/CmsEditScheduledJobInfoDialog.java,v $
 * Date   : $Date: 2005/05/24 15:14:07 $
 * Version: $Revision: 1.10 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2002 - 2005 Alkacon Software (http://www.alkacon.com)
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

package org.opencms.workplace.tools.scheduler;

import org.opencms.configuration.CmsSystemConfiguration;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsContextInfo;
import org.opencms.main.OpenCms;
import org.opencms.monitor.CmsMemoryMonitor;
import org.opencms.scheduler.CmsScheduledJobInfo;
import org.opencms.search.CmsSearchManager;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsCheckboxWidget;
import org.opencms.widgets.CmsComboWidget;
import org.opencms.widgets.CmsInputWidget;
import org.opencms.widgets.CmsVfsFileWidget;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWidgetDialog;
import org.opencms.workplace.CmsWidgetDialogParameter;
import org.opencms.workplace.CmsWorkplaceSettings;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Dialog to edit new and existing scheduled jobs in the administration view.<p>
 * 
 * @author Andreas Zahner (a.zahner@alkacon.com)
 * 
 * @version $Revision: 1.10 $
 * @since 5.9.1
 */
public class CmsEditScheduledJobInfoDialog extends CmsWidgetDialog {

    /** The action to copy a job to edit. */
    public static final String DIALOG_COPYJOB = "copyjob";

    /** Defines which pages are valid for this dialog. */
    public static final String[] PAGES = {"page1", "page2"};

    /** Request parameter name for the job id. */
    public static final String PARAM_JOBID = "jobid";

    /** Request parameter name for the job name. */
    public static final String PARAM_JOBNAME = "jobname";

    /** The job info object that is edited on this dialog. */
    private CmsScheduledJobInfo m_jobInfo;

    /** Stores the value of the request parameter for the job id. */
    private String m_paramJobid;

    /** Stores the value of the request parameter for the job name. */
    private String m_paramJobname;

    /**
     * Public constructor with JSP action element.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsEditScheduledJobInfoDialog(CmsJspActionElement jsp) {

        super(jsp);
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsEditScheduledJobInfoDialog(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * Commits the edited scheduled job to the scheduler.<p>
     */
    public void actionCommit() {

        List errors = new ArrayList();

        try {
            // schedule the edited job
            OpenCms.getScheduleManager().scheduleJob(getCms(), m_jobInfo);
            // clear the HTML list to be up to date after editing
            getSettings().setHtmlList(null);
            // update the XML configuration
            OpenCms.writeConfiguration(CmsSystemConfiguration.class);
        } catch (Throwable t) {
            errors.add(t);
        }

        // set the list of errors to display when saving failed
        setCommitErrors(errors);
    }

    /**
     * Returns the job id parameter value.<p>
     * 
     * @return the job id parameter value
     */
    public String getParamJobid() {

        return m_paramJobid;
    }

    /**
     * Returns the job name parameter value.<p>
     * 
     * @return the job name parameter value
     */
    public String getParamJobname() {

        return m_paramJobname;
    }

    /**
     * Sets the job id parameter value.<p>
     * 
     * @param jobid the job id parameter value
     */
    public void setParamJobid(String jobid) {

        m_paramJobid = jobid;
    }

    /**
     * Sets the job name parameter value.<p>
     * 
     * @param jobname the job name parameter value
     */
    public void setParamJobname(String jobname) {

        m_paramJobname = jobname;
    }

    /**
     * Creates the dialog HTML for all defined widgets of the named dialog (page).<p>
     * 
     * This overwrites the method from the super class to create a layout variation for the widgets.<p>
     * 
     * @param dialog the dialog (page) to get the HTML for
     * @return the dialog HTML for all defined widgets of the named dialog (page)
     */
    protected String createDialogHtml(String dialog) {

        StringBuffer result = new StringBuffer(1024);

        // create widget table
        result.append(createWidgetTableStart());

        // show error header once if there were validation errors
        result.append(createWidgetErrorHeader());

        if (dialog.equals(PAGES[0])) {
            // create the widgets for the first dialog page
            result.append(dialogBlockStart(key(Messages.GUI_EDITOR_LABEL_JOBSETTINGS_BLOCK_0)));
            result.append(createWidgetTableStart());
            result.append(createDialogRowsHtml(0, 4));
            result.append(createWidgetTableEnd());
            result.append(dialogBlockEnd());
            result.append(dialogBlockStart(key(Messages.GUI_EDITOR_LABEL_CONTEXTINFO_BLOCK_0)));
            result.append(createWidgetTableStart());
            result.append(createDialogRowsHtml(5, 11));
            result.append(createWidgetTableEnd());
            result.append(dialogBlockEnd());
        } else if (dialog.equals(PAGES[1])) {
            // create the widget for the second dialog page
            result.append(dialogBlockStart(key(Messages.GUI_EDITOR_LABEL_PARAMETERS_BLOCK_0)));
            result.append(createWidgetTableStart());
            result.append(createDialogRowsHtml(12, 12));
            result.append(createWidgetTableEnd());
            result.append(dialogBlockEnd());
        }

        // close widget table
        result.append(createWidgetTableEnd());

        return result.toString();
    }

    /**
     * Creates the list of widgets for this dialog.<p>
     */
    protected void defineWidgets() {

        // initialize the scheduled job object to use for the dialog
        initScheduledJobObject();

        // required to read the default values for the optional context parameters for the widgets
        CmsContextInfo dC = new CmsContextInfo();

        // widgets to display on the first dialog page
        addWidget(new CmsWidgetDialogParameter(m_jobInfo, "jobName", PAGES[0], new CmsInputWidget()));
        addWidget(new CmsWidgetDialogParameter(m_jobInfo, "className", PAGES[0], new CmsComboWidget(getComboClasses())));
        addWidget(new CmsWidgetDialogParameter(m_jobInfo, "cronExpression", PAGES[0], new CmsComboWidget(getComboCronExpressions())));
        addWidget(new CmsWidgetDialogParameter(m_jobInfo, "reuseInstance", PAGES[0], new CmsCheckboxWidget()));
        addWidget(new CmsWidgetDialogParameter(m_jobInfo, "active", PAGES[0], new CmsCheckboxWidget()));
        addWidget(new CmsWidgetDialogParameter(m_jobInfo, "contextInfo.userName", PAGES[0], new CmsInputWidget()));
        addWidget(new CmsWidgetDialogParameter(m_jobInfo, "contextInfo.projectName", PAGES[0], new CmsInputWidget()));
        addWidget(new CmsWidgetDialogParameter(
            m_jobInfo,
            "contextInfo.siteRoot",
            dC.getSiteRoot(),
            PAGES[0],
            new CmsVfsFileWidget(),
            0,
            1));
        addWidget(new CmsWidgetDialogParameter(
            m_jobInfo,
            "contextInfo.requestedUri",
            dC.getRequestedUri(),
            PAGES[0],
            new CmsVfsFileWidget(),
            0,
            1));
        addWidget(new CmsWidgetDialogParameter(
            m_jobInfo,
            "contextInfo.localeName",
            dC.getLocaleName(),
            PAGES[0],
            new CmsInputWidget(),
            0,
            1));
        addWidget(new CmsWidgetDialogParameter(
            m_jobInfo,
            "contextInfo.encoding",
            dC.getEncoding(),
            PAGES[0],
            new CmsInputWidget(),
            0,
            1));
        addWidget(new CmsWidgetDialogParameter(
            m_jobInfo,
            "contextInfo.remoteAddr",
            dC.getRemoteAddr(),
            PAGES[0],
            new CmsInputWidget(),
            0,
            1));

        // widget to display on the second dialog page
        addWidget(new CmsWidgetDialogParameter(m_jobInfo, "parameters", PAGES[1], new CmsInputWidget()));
    }
    
    /**
     * Returns the example cron class names to show in the combo box.<p>
     * 
     * @return the example cron class names to show in the combo box
     */
    public String getComboClasses() {

        StringBuffer result = new StringBuffer(8);
        result.append(CmsMemoryMonitor.class.getName());
        result.append(":");
        result.append(Messages.get().key(Messages.GUI_EDITOR_CRONCLASS_EXAMPLE1_0));
        result.append("|");
        result.append(CmsSearchManager.class.getName());
        result.append(":");
        result.append(Messages.get().key(Messages.GUI_EDITOR_CRONCLASS_EXAMPLE2_0));
        
        
        return result.toString();
    }
    
    /**
     * Returns the example cron expressions to show in the combo box.<p>
     * 
     * @return the example cron expressions to show in the combo box
     */
    protected String getComboCronExpressions() {

        StringBuffer result = new StringBuffer(8);
        
        // 0 0 3 * * ? (daily at 3 am)
        result.append("0 0 3 * * ?:");
        result.append(Messages.get().key(Messages.GUI_EDITOR_CRONJOB_EXAMPLE1_0));
        // 0 0/30 * * * ? (daily every thirty minutes) 
        result.append("|0 0/30 * * * ?:");
        result.append(Messages.get().key(Messages.GUI_EDITOR_CRONJOB_EXAMPLE2_0));
        // 0 30 8 ? * 4 (every Wednesday at 8:30 am)
        result.append("|0 30 8 ? * 4:");
        result.append(Messages.get().key(Messages.GUI_EDITOR_CRONJOB_EXAMPLE3_0));
        // 0 15 18 15 * ? (on the 15th day of the month at 6:15 pm)
        result.append("|0 15 18 15 * ?:");
        result.append(Messages.get().key(Messages.GUI_EDITOR_CRONJOB_EXAMPLE4_0));
        // 0 45 15 ? * 1 2005-2006 (every Sunday from the year 2005 to 2006 at 3:45 pm)
        result.append("|0 45 15 ? * 1 2005-2006:");
        result.append(Messages.get().key(Messages.GUI_EDITOR_CRONJOB_EXAMPLE5_0));
        return result.toString();
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#getPageArray()
     */
    protected String[] getPageArray() {

        return PAGES;
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initMessages()
     */
    protected void initMessages() {

        // add specific dialog resource bundle
        addMessages(Messages.get().getBundleName());
        // add default resource bundles
        super.initMessages();
    }

    /**
     * Initializes the scheduled job object to work with depending on the dialog state and request parameters.<p>
     * 
     * Three initializations of the scheduled job object on first dialog call are possible:
     * <ul>
     * <li>edit an existing scheduled job</li>
     * <li>create a new scheduled job</li>
     * <li>copy an existing scheduled job and edit it</li>
     * </ul>
     */
    protected void initScheduledJobObject() {

        Object o;

        boolean setActive = false;

        if (CmsStringUtil.isEmpty(getParamAction())
            || CmsDialog.DIALOG_INITIAL.equals(getParamAction())
            || DIALOG_COPYJOB.equals(getParamAction())) {
            // this is the initial dialog call
            if (CmsStringUtil.isNotEmpty(getParamJobid())) {
                // edit or copy an existing job, get the job object from manager
                setActive = OpenCms.getScheduleManager().getJob(getParamJobid()).isActive();
                o = OpenCms.getScheduleManager().getJob(getParamJobid()).clone();
            } else {
                // create a new job for the new job dialog
                o = null;
            }
        } else {
            // this is not the initial call, get the job object from session
            o = getDialogObject();
        }

        if (!(o instanceof CmsScheduledJobInfo)) {
            // create a new job info object
            m_jobInfo = new CmsScheduledJobInfo();
            m_jobInfo.setContextInfo(new CmsContextInfo());
        } else {
            // reuse job info object stored in session
            m_jobInfo = (CmsScheduledJobInfo)o;
        }

        if (setActive) {
            // initial call of edit an existing job, set active state of cloned job
            m_jobInfo.setActive(true);
        }

        if (DIALOG_COPYJOB.equals(getParamAction())) {
            // initial call of copy job action, clear the job id of the cloned job
            m_jobInfo.clearId();
        }
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initWorkplaceRequestValues(org.opencms.workplace.CmsWorkplaceSettings, javax.servlet.http.HttpServletRequest)
     */
    protected void initWorkplaceRequestValues(CmsWorkplaceSettings settings, HttpServletRequest request) {

        // initialize parameters and dialog actions in super implementation
        super.initWorkplaceRequestValues(settings, request);

        // save the current state of the job (may be changed because of the widget values)
        setDialogObject(m_jobInfo);
    }
}