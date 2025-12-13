from dash import html, dcc

# Defines Signup page layout
def layout_signup():
    return html.Div(

        # Main container
        style={
            "display": "flex",
            "justifyContent": "center",
            "alignItems": "center",
            "height": "100vh",
            "background": "#eef1f5"
        },
        children=[
            # Sign up container
            html.Div(
                style={
                    "background": "white",
                    "padding": "40px",
                    "borderRadius": "8px",
                    "boxShadow": "0px 4px 10px rgba(0,0,0,0.1)",
                    "width": "350px",
                    "textAlign": "center"
                },
                children=[
                    html.H2("Create Account"),

                    # Username Input
                    dcc.Input(id="signup-user", type="text", placeholder="Username",
                              style={"width": "100%", "marginBottom": "10px", "padding": "10px"}),

                    # Password Input
                    dcc.Input(id="signup-pass", type="password", placeholder="Password",
                              style={"width": "100%", "marginBottom": "10px", "padding": "10px"}),

                    # Confirm Password Input
                    dcc.Input(id="signup-confirm", type="password", placeholder="Confirm Password",
                              style={"width": "100%", "marginBottom": "20px", "padding": "10px"}),

                    # Submit Button
                    html.Button("Sign Up", id="signup-button", n_clicks=0,
                                style={"width": "100%", "padding": "12px",
                                       "background": "#1f3a93", "color": "white",
                                       "border": "none", "borderRadius": "4px"}),

                    # Feedback message
                    html.Div(id="signup-message", style={"marginTop": "10px", "color": "red"}),

                    html.Br(),
                    # Navigation link
                    dcc.Link("Return to Login", href="/login", style={"marginTop": "10px", "display": "block"})
                ]
            )
        ]
    )
