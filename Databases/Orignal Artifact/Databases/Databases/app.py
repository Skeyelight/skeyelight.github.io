import os
from dotenv import load_dotenv
import dash
from dash import html, dcc
import dash_bootstrap_components as dbc
import pandas as pd
from crud_animals import AnimalShelter

# Callback registration
from callbacks.callbacks_auth import register_auth_callbacks
from callbacks.callbacks_navigation import register_navigation_callbacks
from callbacks.callbacks_dashboard import register_dashboard_callbacks
from callbacks.callbacks_admin import register_admin_callbacks

# Load env
load_dotenv()

# Initialize App
app = dash.Dash(
    __name__,
    suppress_callback_exceptions=True,
    external_stylesheets=[dbc.themes.BOOTSTRAP]
)

# Set the secret key
app.server.secret_key = os.getenv("SECRET_KEY")

# Database Connection
db = AnimalShelter()

# Load initial records
df_initial = pd.DataFrame.from_records(db.read({}))
if "_id" in df_initial.columns:
    df_initial = df_initial.drop(columns=["_id"])

# App Wrapper
app.layout = html.Div([
    dcc.Location(id="url", refresh=False),
    dcc.Store(id="session-store", storage_type="session"),
    html.Div(id="page-content")
])

# Register Callbacks
register_auth_callbacks(app)
register_navigation_callbacks(app)
register_dashboard_callbacks(app, db, df_initial)
register_admin_callbacks(app, db)

# Run app
if __name__ == "__main__":
    app.run(debug=True)