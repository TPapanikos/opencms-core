/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
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

package org.opencms.ui.login;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsUser;
import org.opencms.i18n.CmsEncoder;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.ui.A_CmsUI;
import org.opencms.ui.CmsVaadinErrorHandler;
import org.opencms.ui.CmsVaadinUtils;
import org.opencms.ui.Messages;
import org.opencms.ui.apps.CmsAppWorkplaceUi;
import org.opencms.ui.components.CmsBasicDialog;
import org.opencms.ui.components.CmsBasicDialog.DialogWidth;
import org.opencms.ui.login.CmsLoginController.CmsLoginTargetInfo;
import org.opencms.ui.login.CmsLoginHelper.LoginParameters;
import org.opencms.ui.shared.CmsVaadinConstants;
import org.opencms.util.CmsFileUtil;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsWorkplace;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Version;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * The UI class for the Vaadin-based login dialog.<p>
 */
@Theme("opencms")
public class CmsLoginUI extends A_CmsUI implements I_CmsLoginUI {

    /**
     * Parameters which are initialized during the initial page load of the login dialog.<p>
     */
    public static class Parameters implements Serializable {

        /** The serial version id. */
        private static final long serialVersionUID = -4885232184680664315L;

        /** The locale. */
        public Locale m_locale;

        /** The PC type (public or private). */
        public String m_pcType;

        /** The preselected OU. */
        public String m_preselectedOu;

        /** The requested resource. */
        public String m_requestedResource;

        /** The requested workplace app path. */
        public String m_requestedWorkplaceApp;

        /**
         * Creates a new instance.<p>
         *
         * @param pcType the PC type
         * @param preselectedOu the preselected OU
         * @param locale the locale
         * @param requestedResource the requested resource
         * @param requestedWorkplaceApp the requested workplace app path
         */
        public Parameters(
            String pcType,
            String preselectedOu,
            Locale locale,
            String requestedResource,
            String requestedWorkplaceApp) {

            m_pcType = pcType;
            m_preselectedOu = preselectedOu;
            m_locale = locale;
            m_requestedResource = requestedResource;
            m_requestedWorkplaceApp = requestedWorkplaceApp;
        }

        /**
         * Gets the locale.<p>
         *
         * @return the locale
         */
        public Locale getLocale() {

            return m_locale;
        }

        /**
         * Gets the PC type (private or public).<p>
         *
         * @return the pc type
         */
        public String getPcType() {

            return m_pcType;

        }

        /**
         * Gets the preselected OU.<p>
         *
         * @return the preselected OU
         */
        public String getPreselectedOu() {

            return m_preselectedOu;
        }

        /**
         * Gets the requested resource path.<p>
         *
         * @return the requested resource path
         */
        public String getRequestedResource() {

            return m_requestedResource;
        }

        /**
         * Returns the requested workplace app path.<p>
         *
         * @return the requested workplace app path
         */
        public String getRequestedWorkplaceApp() {

            return m_requestedWorkplaceApp;
        }

    }

    /**
     * Attribute used to store initialization data when the UI is first loaded.
     */
    public static final String INIT_DATA_SESSION_ATTR = "CmsLoginUI_initData";

    /** The admin CMS context. */
    static CmsObject m_adminCms;

    /** Logger instance for this class. */
    private static final Log LOG = CmsLog.getLog(CmsLoginUI.class);

    /** Serial version id. */
    private static final long serialVersionUID = 1L;

    /** The login controller. */
    private CmsLoginController m_controller;

    /** The login form. */
    private CmsLoginForm m_loginForm;

    /** The widget used to open the login target. */
    private CmsLoginTargetOpener m_targetOpener = null;

    /**
     * Returns the initial HTML for the Vaadin based login dialog.<p>
     *
     * @param cms the current cms context
     * @param request the request
     *
     * @return the initial page HTML for the Vaadin login dialog
     */
    public static String displayVaadinLoginDialog(CmsObject cms, HttpServletRequest request) {

        CmsLoginHelper.LoginParameters params = CmsLoginHelper.getLoginParameters(cms, request, false);
        request.getSession().setAttribute(CmsLoginUI.INIT_DATA_SESSION_ATTR, params);
        try {
            byte[] pageBytes = CmsFileUtil.readFully(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(
                    "org/opencms/ui/login/login-page.html"));
            String page = new String(pageBytes, "UTF-8");
            CmsMacroResolver resolver = new CmsMacroResolver();
            String context = OpenCms.getSystemInfo().getContextPath();
            String vaadinDir = CmsStringUtil.joinPaths(context, "VAADIN/");
            String vaadinVersion = Version.getFullVersion();
            String vaadinServlet = CmsStringUtil.joinPaths(context, "workplace/");
            String vaadinBootstrap = CmsStringUtil.joinPaths(context, "VAADIN/vaadinBootstrap.js");
            String autocomplete = params.isPrivatePc() ? "on" : "off";

            String cmsLogo = OpenCms.getSystemInfo().getContextPath()
                + CmsWorkplace.RFS_PATH_RESOURCES
                + "commons/login_logo.png";

            resolver.addMacro("loadingHtml", CmsVaadinConstants.LOADING_INDICATOR_HTML);
            resolver.addMacro("vaadinDir", vaadinDir);
            resolver.addMacro("vaadinVersion", vaadinVersion);
            resolver.addMacro("vaadinServlet", vaadinServlet);
            resolver.addMacro("vaadinBootstrap", vaadinBootstrap);
            resolver.addMacro("cmsLogo", cmsLogo);
            resolver.addMacro("autocomplete", autocomplete);
            resolver.addMacro("title", CmsLoginHelper.getTitle(params.getLocale()));
            if (params.isPrivatePc()) {
                resolver.addMacro(
                    "hiddenPasswordField",
                    "      <input type=\"password\" id=\"hidden-password\" name=\"ocPword\" autocomplete=\"%(autocomplete)\" >");
            }
            if (params.getUsername() != null) {
                resolver.addMacro("predefUser", "value=\"" + CmsEncoder.escapeXml(params.getUsername()) + "\"");
            }
            page = resolver.resolveMacros(page);
            return page;
        } catch (Exception e) {
            LOG.error("Failed to display login dialog.", e);
            return "<!--Error-->";
        }
    }

    /**
     * Returns the bootstrap html fragment required to display the login dialog.<p>
     *
     * @param cms the cms context
     * @param request the request
     *
     * @return the html fragment
     *
     * @throws IOException in case reading the html template fails
     */
    public static String generateLoginHtmlFragment(CmsObject cms, VaadinRequest request) throws IOException {

        LoginParameters parameters = CmsLoginHelper.getLoginParameters(cms, (HttpServletRequest)request, true);
        request.getWrappedSession().setAttribute(CmsLoginUI.INIT_DATA_SESSION_ATTR, parameters);
        byte[] pageBytes;

        pageBytes = CmsFileUtil.readFully(
            Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "org/opencms/ui/login/login-fragment.html"));

        String html = new String(pageBytes, "UTF-8");
        String autocomplete = ((parameters.getPcType() == null)
            || parameters.getPcType().equals(CmsLoginHelper.PCTYPE_PRIVATE)) ? "on" : "off";
        CmsMacroResolver resolver = new CmsMacroResolver();
        resolver.addMacro("autocompplete", autocomplete);
        if ((parameters.getPcType() == null) || parameters.getPcType().equals(CmsLoginHelper.PCTYPE_PRIVATE)) {
            resolver.addMacro(
                "hiddenPasswordField",
                "      <input type=\"password\" id=\"hidden-password\" name=\"ocPword\" autocomplete=\"%(autocomplete)\" >");
        }
        if (parameters.getUsername() != null) {
            resolver.addMacro("predefUser", "value=\"" + CmsEncoder.escapeXml(parameters.getUsername()) + "\"");
        }
        html = resolver.resolveMacros(html);
        return html;
    }

    /**
     * Sets the admin CMS object.<p>
     *
     * @param cms the admin cms object
     */
    public static void setAdminCmsObject(CmsObject cms) {

        m_adminCms = cms;
    }

    /**
     * @see org.opencms.ui.login.I_CmsLoginUI#getOrgUnit()
     */
    public String getOrgUnit() {

        String result = m_loginForm.getOrgUnit();
        if (result == null) {
            result = "";
        }
        return result;

    }

    /**
     * @see org.opencms.ui.login.I_CmsLoginUI#getPassword()
     */
    public String getPassword() {

        return m_loginForm.getPassword();
    }

    /**
     * @see org.opencms.ui.login.I_CmsLoginUI#getPcType()
     */
    public String getPcType() {

        String result = m_loginForm.getPcType();
        if (result == null) {
            result = "public";
        }
        return result;
    }

    /**
     * @see org.opencms.ui.login.I_CmsLoginUI#getUser()
     */
    public String getUser() {

        return m_loginForm.getUser();
    }

    /**
     * @see org.opencms.ui.login.I_CmsLoginUI#openLoginTarget(org.opencms.ui.login.CmsLoginController.CmsLoginTargetInfo)
     */
    public void openLoginTarget(CmsLoginTargetInfo targetInfo) {

        // login was successful, remove login init data from session
        VaadinService.getCurrentRequest().getWrappedSession().removeAttribute(INIT_DATA_SESSION_ATTR);
        m_targetOpener.openTarget(targetInfo.getTarget(), targetInfo.getUser(), targetInfo.getPassword());
    }

    /**
     * @see org.opencms.ui.login.I_CmsLoginUI#setSelectableOrgUnits(java.util.List)
     */
    public void setSelectableOrgUnits(List<CmsOrganizationalUnit> ous) {

        m_loginForm.setSelectableOrgUnits(ous);
    }

    /**
     * @see org.opencms.ui.login.I_CmsLoginUI#showAlreadyLoggedIn()
     */
    public void showAlreadyLoggedIn() {

        throw new UnsupportedOperationException("Not implemented yet, this shouldn't even be called.");
    }

    /**
     * @see org.opencms.ui.login.I_CmsLoginUI#showForgotPasswordView(java.lang.String)
     */
    public void showForgotPasswordView(String authToken) {

        try {
            CmsTokenValidator validator = new CmsTokenValidator();
            String validationResult = validator.validateToken(
                A_CmsUI.getCmsObject(),
                authToken,
                OpenCms.getLoginManager().getTokenLifetime());
            if (validationResult == null) {
                CmsUser user = validator.getUser();
                CmsSetPasswordDialog dlg = new CmsSetPasswordDialog(m_adminCms, user, getLocale());
                A_CmsUI.get().setContentToDialog(
                    Messages.get().getBundle(A_CmsUI.get().getLocale()).key(Messages.GUI_PWCHANGE_HEADER_0)
                        + user.getName(),
                    dlg);
            } else {
                A_CmsUI.get().setError(
                    Messages.get().getBundle(A_CmsUI.get().getLocale()).key(Messages.GUI_PWCHANGE_INVALID_TOKEN_0));
                LOG.info("Invalid authorization token: " + authToken + " / " + validationResult);
            }
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * @see org.opencms.ui.login.I_CmsLoginUI#showLoginView(java.lang.String)
     */
    public void showLoginView(String preselectedOu) {

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        m_targetOpener = new CmsLoginTargetOpener(A_CmsUI.get());
        //content.setExpandRatio(m_targetOpener, 0f);
        content.addComponent(m_loginForm);
        content.setComponentAlignment(m_loginForm, Alignment.MIDDLE_CENTER);
        content.setExpandRatio(m_loginForm, 1);

        setContent(content);
        if (preselectedOu == null) {
            preselectedOu = "/";
        }
        m_loginForm.selectOrgUnit(preselectedOu);

    }

    /**
     * @see org.opencms.ui.login.I_CmsLoginUI#showPasswordResetDialog()
     */
    public void showPasswordResetDialog() {

        String caption = CmsVaadinUtils.getMessageText(Messages.GUI_PWCHANGE_FORGOT_PASSWORD_0);
        A_CmsUI r = A_CmsUI.get();
        r.setContent(new Label());
        Window window = CmsBasicDialog.prepareWindow(DialogWidth.narrow);
        CmsBasicDialog dialog = new CmsBasicDialog();
        VerticalLayout result = new VerticalLayout();
        dialog.setContent(result);
        window.setContent(dialog);
        window.setCaption(caption);
        window.setClosable(true);
        final CmsForgotPasswordDialog forgotPassword = new CmsForgotPasswordDialog();
        window.addCloseListener(new CloseListener() {

            /** Serial version id. */
            private static final long serialVersionUID = 1L;

            public void windowClose(CloseEvent e) {

                forgotPassword.cancel();
            }

        });
        for (Button button : forgotPassword.getButtons()) {
            dialog.addButton(button);
        }

        r.addWindow(window);
        window.center();
        VerticalLayout vl = result;
        vl.addComponent(forgotPassword);
    }

    /**
     * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
     */
    @Override
    protected void init(VaadinRequest request) {

        addStyleName("login-dialog");
        LoginParameters params = (LoginParameters)(request.getWrappedSession().getAttribute(INIT_DATA_SESSION_ATTR));
        if (params == null) {
            params = CmsLoginHelper.getLoginParameters(getCmsObject(), (HttpServletRequest)request, true);
            request.getWrappedSession().setAttribute(CmsLoginUI.INIT_DATA_SESSION_ATTR, params);
        }
        VaadinSession.getCurrent().setErrorHandler(new CmsVaadinErrorHandler());
        m_controller = new CmsLoginController(m_adminCms, params);
        m_controller.setUi(this);
        setLocale(params.getLocale());
        m_loginForm = new CmsLoginForm(m_controller, params.getLocale());
        m_controller.onInit();
        getPage().setTitle(
            CmsAppWorkplaceUi.WINDOW_TITLE_PREFIX
                + CmsVaadinUtils.getMessageText(org.opencms.workplace.Messages.GUI_LOGIN_HEADLINE_0));
    }
}
