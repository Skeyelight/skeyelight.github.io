from dash import Input, Output, no_update, html, dcc
from layouts.dashboard_layout import layout_dashboard
from layouts.login_layout import layout_login
from layouts.signup_layout import layout_signup
from layouts.admin_layout import layout_admin

def register_navigation_callbacks(app):

    # Page Routing
    @app.callback(
        Output("page-content", "children"),
        [
            Input("url", "pathname"),
            Input("session-store", "data")
        ]
    )
    def display_page(pathname, session_data):

        # Gets login status and role
        is_logged_in = session_data and session_data.get("logged_in")
        user_role = session_data.get("role", "user") if is_logged_in else None

        # Admin Route
        if pathname == "/admin":
            if not is_logged_in:
                return layout_login()

            # Allow access for admin role
            if user_role == "admin":
                return layout_admin()
            else:
                # Access denied message for non-admins
                return html.Div(style={"textAlign": "center", "marginTop": "50px"}, children=[
                    html.H1("Access Denied"),
                    html.P("You do not have permission to view this page."),
                    dcc.Link("← Return to Dashboard", href="/dashboard", style={"fontSize": "18px"})
                ])

        # Show dashboard after login
        if is_logged_in:
            return layout_dashboard()

        # User Route
        if pathname == "/signup":
            return layout_signup()

        # Default: show login page
        return layout_login()

    # Sign Up Navigation
    @app.callback(
        Output("url", "pathname", allow_duplicate=True),
        Input("sign-up-button", "n_clicks"),
        prevent_initial_call=True
    )

    # Go to Sign Up page when button is clicked
    def go_to_signup(n_clicks):
        if n_clicks:
            return "/signup"
        return no_update

    # Go to the admin page button is clicked
    @app.callback(
        Output("url", "pathname", allow_duplicate=True),
        Input("admin-button", "n_clicks"),
        prevent_initial_call=True
    )
    def go_to_admin(n_clicks):
        if n_clicks:
            return "/admin"
        return no_update

    # Logout when button is clicked
    @app.callback(
        [Output("session-store", "data", allow_duplicate=True),
         Output("url", "pathname", allow_duplicate=True)],
        Input("logout-button", "n_clicks"),
        prevent_initial_call=True
    )


    # Clears session data and redirects to login when logged out
    def logout_user(n_clicks):
        if n_clicks:
            return None, "/login"
        return no_update, no_update