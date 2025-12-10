from dash import Input, Output, State, no_update
from auth import UserAuth


def register_auth_callbacks(app):
    user_auth = UserAuth()

    # Signup Callback
    @app.callback(
        Output("signup-message", "children"),
        Input("signup-button", "n_clicks"),
        State("signup-user", "value"),
        State("signup-pass", "value"),
        State("signup-confirm", "value"),
        prevent_initial_call=True
    )
    def handle_signup(n_clicks, username, password, confirm):

        # Check if button was clicked
        if n_clicks is None or n_clicks == 0:
            return no_update

        if password != confirm:
            return "Passwords do not match."

        if not username or not password:
            return "All fields are required."

        success, message = user_auth.create_user(username, password)

        if success:
            return "Account created successfully. You may now login."
        else:
            return f"{message}"

    # Login Callback
    @app.callback(
        Output("session-store", "data"),
        Output("login-message", "children"),
        Input("login-button", "n_clicks"),
        State("login-user", "value"),
        State("login-pass", "value"),
        prevent_initial_call=True
    )
    def handle_login(n_clicks, username, password):

        if n_clicks is None or n_clicks == 0:
            return no_update, no_update

        if not username or not password:
            return no_update, "Username and password required."

        is_valid = user_auth.validate_user(username, password)

        if is_valid:
            # Fetch Role
            role = user_auth.get_role(username)

            # Store role in session
            session_data = {"logged_in": True, "user": username, "role": role}
            return session_data, ""
        else:
            return no_update, "Invalid username or password."