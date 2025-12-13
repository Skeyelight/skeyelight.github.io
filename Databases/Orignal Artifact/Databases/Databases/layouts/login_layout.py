from dash import html, dcc
import base64

# Defines Login page layout
def layout_login():
    # Load logo
    image_filename = "resources/Grazioso Salvare Logo.png"
    try:
        encoded_image = base64.b64encode(open(image_filename, "rb").read()).decode()
    except FileNotFoundError:
        encoded_image = ""

    # Page wrapper
    return html.Div(
        style={
            "display": "flex",
            "justifyContent": "center",
            "alignItems": "center",
            "height": "100vh",
            "background": "#eef1f5"
        },
        children=[
            # Login Card
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
                    # Logo
                    html.Img(src=f"data:image/png;base64,{encoded_image}",
                             style={"height": "200px", "marginBottom": "20px"}),

                    html.H2("Login"),

                    # Username Input
                    dcc.Input(id="login-user", type="text", placeholder="Username",
                              style={"width": "100%", "marginBottom": "10px", "padding": "10px", "boxSizing": "border-box"}),

                    # Password Input
                    dcc.Input(id="login-pass", type="password", placeholder="Password",
                              style={"width": "100%", "marginBottom": "20px", "padding": "10px", "boxSizing": "border-box"}),

                    # Buttons Row
                    html.Div(
                        style={
                            "display": "flex",
                            "justifyContent": "center",
                            "gap": "10px"
                        },
                        children=[
                            # Login Button
                            html.Button("Login", id="login-button", n_clicks=0,
                                        style={
                                            "flex": "1",
                                            "padding": "12px",
                                            "background": "#1f3a93",
                                            "color": "white",
                                            "border": "none",
                                            "borderRadius": "4px",
                                            "cursor": "pointer"
                                        }),

                            # Sign up Button
                            html.Button("Sign up", id="sign-up-button", n_clicks=0,
                                        style={
                                            "flex": "1",
                                            "padding": "12px",
                                            "background": "#1f3a93",
                                            "color": "white",
                                            "border": "none",
                                            "borderRadius": "4px",
                                            "cursor": "pointer"
                                        }),
                        ]
                    ),

                    html.Br(),

                    # Error Messages
                    html.Div(id="login-message", style={"marginTop": "10px", "color": "red"}),
                ]
            )
        ]
    )