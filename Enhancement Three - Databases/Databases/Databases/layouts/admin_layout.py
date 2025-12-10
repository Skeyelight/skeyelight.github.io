# layouts/admin_layout.py
from dash import html, dcc

# Defines layout for Admin page
def layout_admin():

    # Style for all input boxes
    input_style = {
        "width": "100%",
        "marginBottom": "10px",
        "padding": "8px",
        "boxSizing": "border-box"
    }

    # Style for the grouped sections
    group_box_style = {
        "flex": "1",
        "minWidth": "300px",
        "padding": "15px",
        "border": "1px solid #eee",
        "borderRadius": "8px",
        "backgroundColor": "#f9f9f9",
        "boxSizing": "border-box"
    }

    return html.Div(
        style={"padding": "40px", "maxWidth": "1000px", "margin": "0 auto"},
        children=[

            # Header
            html.H1("Admin Panel: Manage Records"),
            dcc.Link("← Back to Dashboard", href="/dashboard", style={"marginBottom": "20px", "display": "block"}),

            # Tabs section
            dcc.Tabs([
                # Add new animal tab
                dcc.Tab(label='Add New Animal', children=[
                    html.Div(style={"padding": "20px", "border": "1px solid #ddd", "borderTop": "none"}, children=[
                        html.H3("Create New Record"),
                        html.P("Animal Type is the only required field."),

                        # Container
                        html.Div(
                            style={
                                "display": "flex",
                                "gap": "20px",
                                "flexWrap": "wrap"
                            },
                            children=[
                                # Basic Info input
                                html.Div(style=group_box_style, children=[
                                    html.H5("Basic Information"),
                                    dcc.Input(id="new-type", type="text", placeholder="Type", style=input_style),
                                    dcc.Input(id="new-name", type="text", placeholder="Name", style=input_style),
                                    dcc.Input(id="new-breed", type="text", placeholder="Breed", style=input_style),
                                    dcc.Input(id="new-color", type="text", placeholder="Color", style=input_style),
                                    dcc.Input(id="new-sex", type="text", placeholder="Sex (e.g., Intact Male)",
                                              style=input_style),
                                    dcc.Input(id="new-dob", type="text", placeholder="Date of Birth (YYYY-MM-DD)",
                                              style=input_style),
                                ]),

                                # Outcome & Age input
                                html.Div(style=group_box_style, children=[
                                    html.H5("Outcome Details"),
                                    dcc.Input(id="new-outcome-type", type="text", placeholder="Outcome Type (e.g., Adoption)",
                                              style=input_style),
                                    dcc.Input(id="new-outcome-subtype", type="text", placeholder="Outcome Subtype",
                                              style=input_style),
                                    dcc.Input(id="new-datetime", type="text",
                                              placeholder="Outcome Date/Time (YYYY-MM-DD HH:MM:SS)", style=input_style),
                                    dcc.Input(id="new-age-weeks", type="number", placeholder="Age in Weeks",
                                              style=input_style),
                                ]),

                                # Location input
                                html.Div(style=group_box_style, children=[
                                    html.H5("Location"),
                                    dcc.Input(id="new-lat", type="number", placeholder="Latitude", style=input_style),
                                    dcc.Input(id="new-long", type="number", placeholder="Longitude", style=input_style),
                                ])
                            ]
                        ),

                        html.Br(),

                        # Add Animal Button
                        html.Button("Add Animal", id="btn-add", n_clicks=0,
                                    style={"backgroundColor": "#28a745", "color": "white", "padding": "12px 24px",
                                           "border": "none", "borderRadius": "5px", "fontSize": "16px"}),
                        # Action Feedback
                        html.Div(id="msg-add", style={"marginTop": "15px", "fontWeight": "bold"})
                    ])
                ]),

                # Update / Delete tab
                dcc.Tab(label='Update or Delete', children=[
                    html.Div(style={"padding": "20px", "border": "1px solid #ddd", "borderTop": "none"}, children=[
                        html.H3("Manage Existing Record"),
                        html.P("Enter the Animal ID to target. Fill only the fields you wish to change."),

                        dcc.Input(id="target-id", type="text", placeholder="Target Animal ID (Required)",
                                  style={"width": "100%", "marginBottom": "20px", "padding": "10px",
                                         "fontWeight": "bold"}),

                        html.Hr(),

                        # Container
                        html.Div(
                            style={
                                "display": "flex",
                                "gap": "20px",
                                "flexWrap": "wrap"
                            },
                            children=[
                                # Basic Info input
                                html.Div(style=group_box_style, children=[
                                    html.H5("Update Basic Info"),
                                    dcc.Input(id="new-type", type="text", placeholder="Type", style=input_style),
                                    dcc.Input(id="up-name", type="text", placeholder="New Name", style=input_style),
                                    dcc.Input(id="up-breed", type="text", placeholder="New Breed", style=input_style),
                                    dcc.Input(id="up-color", type="text", placeholder="New Color", style=input_style),
                                    dcc.Input(id="up-sex", type="text", placeholder="New Sex", style=input_style),
                                    dcc.Input(id="up-dob", type="text", placeholder="New DOB (YYYY-MM-DD)",
                                              style=input_style),
                                ]),

                                # Outcome & Age input
                                html.Div(style=group_box_style, children=[
                                    html.H5("Update Outcome Info"),
                                    dcc.Input(id="up-outcome-type", type="text", placeholder="New Outcome Type",
                                              style=input_style),
                                    dcc.Input(id="up-outcome-subtype", type="text", placeholder="New Outcome Subtype",
                                              style=input_style),
                                    dcc.Input(id="up-datetime", type="text", placeholder="New Date/Time",
                                              style=input_style),
                                    dcc.Input(id="up-age-weeks", type="number", placeholder="New Age (Weeks)",
                                              style=input_style),
                                ]),

                                # Location input
                                html.Div(style=group_box_style, children=[
                                    html.H5("Update Location"),
                                    dcc.Input(id="up-lat", type="number", placeholder="New Latitude",
                                              style=input_style),
                                    dcc.Input(id="up-long", type="number", placeholder="New Longitude",
                                              style=input_style),
                                ])
                            ]
                        ),

                        # Update and Delete buttons
                        html.Div(style={"display": "flex", "gap": "10px", "marginTop": "20px"}, children=[
                            html.Button("Update Record", id="btn-update", n_clicks=0,
                                        style={"backgroundColor": "#007bff", "color": "white", "padding": "10px 20px",
                                               "border": "none", "borderRadius": "5px"}),
                            html.Button("Delete Record", id="btn-delete", n_clicks=0,
                                        style={"backgroundColor": "#dc3545", "color": "white", "padding": "10px 20px",
                                               "border": "none", "borderRadius": "5px"}),
                        ]),

                        # Action Feedback
                        html.Div(id="msg-manage", style={"marginTop": "15px", "fontWeight": "bold"})
                    ])
                ])
            ])
        ]
    )