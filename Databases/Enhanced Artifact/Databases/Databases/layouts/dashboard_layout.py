from dash import html, dcc
from dash import dash_table
import base64

# Defines main dashboard layout
def layout_dashboard():
    # Load logo
    image_filename = "resources/Grazioso Salvare Logo.png"
    try:
        encoded_image = base64.b64encode(open(image_filename, "rb").read()).decode()
    except FileNotFoundError:
        encoded_image = ""

    return html.Div(children=[

        # Header
        html.Div(
            style={
                "display": "flex",
                "alignItems": "center",
                "justifyContent": "center",
                "gap": "20px",
                "padding": "20px",
                "backgroundColor": "#f8f9fa",
                "borderBottom": "2px solid #ddd",
                "position": "relative"
            },
            children=[
                # Logo
                html.Img(src=f"data:image/png;base64,{encoded_image}",
                         style={"height": "150px"}),

                # Title
                html.Div(children=[
                    html.H1("CS-499 Enhancement", style={"color": "#333", "margin": "0"}),
                    html.H2("Thomas Davis", style={"color": "#666", "margin": "0", "fontSize": "18px"})
                ]),

                # Buton Container
                html.Div(
                    style={
                        "position": "absolute",
                        "right": "20px",
                        "top": "50%",
                        "transform": "translateY(-50%)",
                        "display": "flex",
                        "gap": "10px"
                    },
                    children=[
                        # Admin Button
                        html.Button("Admin", id="admin-button", n_clicks=0,
                                    style={
                                        "padding": "10px 20px", "backgroundColor": "#1f3a93", "color": "white",
                                        "border": "none", "borderRadius": "5px", "cursor": "pointer"
                        }),

                        # Logout Button
                        html.Button("Logout", id="logout-button", n_clicks=0,
                                    style={
                                        "padding": "10px 20px", "backgroundColor": "#dc3545", "color": "white",
                                        "border": "none", "borderRadius": "5px", "cursor": "pointer"
                                    })
                    ]
                )
            ]
        ),

        html.Hr(),

        # Radio Buttons
        html.Div(
            style={"textAlign": "center", "padding": "10px", "backgroundColor": "#fff"},
            children=[
                dcc.RadioItems(
                    id='filter-type-radio',
                    options=[
                        {'label': 'Water Rescue', 'value': 'water'},
                        {'label': 'Mountain Rescue', 'value': 'mountain'},
                        {'label': 'Disaster', 'value': 'disaster'},
                        {'label': 'Reset', 'value': 'reset'}
                    ],
                    value='reset',
                    labelStyle={'display': 'inline-block', 'marginRight': '20px', 'fontWeight': 'bold', 'color': '#333'},
                    inputStyle={'marginRight': '5px'}
                )
            ]
        ),

        # Dropdowns
        html.Div(
            style={"display": "flex", "gap": "20px", "padding": "10px", "backgroundColor": "#fff",
                   "justifyContent": "center"},
            children=[
                dcc.Dropdown(
                    id="animal-type-dropdown",
                    options=[
                        {"label": "Dog", "value": "Dog"},
                        {"label": "Cat", "value": "Cat"},
                        {"label": "Other", "value": "Other"},
                    ],
                    placeholder="Select Animal Type",
                    clearable=True,
                    style={"width": "250px"}
                ),
                dcc.Dropdown(
                    id="breed-dropdown",
                    placeholder="Search for Breed...",
                    searchable=True,
                    clearable=True,
                    style={"width": "350px"}
                ),
            ]
        ),

        # Table Container
        html.Div(
            style={"padding": "0 20px"},
            children=[
                dash_table.DataTable(
                    id="datatable-id",
                    filter_action="native",
                    sort_action="native",
                    sort_mode="multi",
                    row_selectable="single",
                    page_action="native",
                    page_current=0,
                    page_size=10,
                    style_table={
                        "height": "400px",
                        "overflowY": "auto",
                        "borderRadius": "8px",
                        "border": "1px solid #e0e0e0",
                        "boxShadow": "0 2px 4px rgba(0,0,0,0.05)"
                    },
                    style_header={
                        "backgroundColor": "#1f3a93",
                        "color": "white",
                        "fontWeight": "bold",
                        "textAlign": "left",
                        "padding": "12px",
                        "fontSize": "14px",
                        "border": "none"
                    },
                    style_cell={
                        "textAlign": "left",
                        "padding": "12px",
                        "fontFamily": "'Segoe UI', 'Roboto', 'Helvetica', sans-serif",
                        "fontSize": "13px",
                        "color": "#333",
                        "minWidth": "100px"
                    },
                    style_data={
                        "borderBottom": "1px solid #f0f0f0"
                    },
                    style_data_conditional=[
                        {
                            "if": {"row_index": "odd"},
                            "backgroundColor": "#f9f9f9"
                        },
                        {
                            "if": {"state": "active"},
                            "backgroundColor": "rgba(31, 58, 147, 0.1)",
                            "border": "1px solid #1f3a93"
                        },
                        {
                            "if": {"state": "selected"},
                            "backgroundColor": "rgba(31, 58, 147, 0.2)",
                            "border": "1px solid #1f3a93"
                        }
                    ]
                )
            ]
        ),

        html.Br(),

        # Graph + Map
        html.Div(
            style={
                "display": "flex",
                "gap": "20px",
                "padding": "20px",
                "justifyContent": "center"
            },

            children=[
                # Graph
                html.Div(id="graph-id",
                         style={
                             "width": "45%",
                             "boxShadow": "0 2px 10px rgba(0,0,0,0.05)",
                             "padding": "10px",
                             "borderRadius": "8px",
                             "backgroundColor": "white",
                             "display": "flex",
                             "justifyContent": "center",
                             "alignItems": "center"
                         }),
                # Map
                html.Div(id="map-id",
                         style={
                             "width": "45%",
                             "boxShadow": "0 2px 10px rgba(0,0,0,0.05)",
                             "padding": "10px",
                             "borderRadius": "8px",
                             "backgroundColor": "white"
                         })
            ]
        )
    ])