import dash_leaflet as dl
import pandas as pd
import plotly.express as px
from dash import Input, Output, dcc, html


def register_dashboard_callbacks(app, db, df_initial):

    # Breed Dropdown Callback
    @app.callback(
        Output("breed-dropdown", "options"),
        Input("animal-type-dropdown", "value")
    )
    # Updates the Breed dropdown based on the selected animal type
    def update_breed_options(selected_type):
        df = df_initial
        if not selected_type:
            breeds = sorted(df["breed"].dropna().unique().tolist())
        else:
            breeds = sorted(
                df[df["animal_type"] == selected_type]["breed"].dropna().unique().tolist()
            )
        return [{"label": b, "value": b} for b in breeds]

    # Table, Radio Buttons, and Dropdowns Callbacks
    @app.callback(
        [Output("datatable-id", "data"), Output("datatable-id", "columns")],
        [Input("animal-type-dropdown", "value"),
         Input("breed-dropdown", "value"),
         Input("filter-type-radio", "value")]
    )

    # Applies filters to the table
    def filter_table(selected_type, selected_breed, filter_type):
        query = {}

        # Apply Radio Button Filters (Rescue Types)
        # This was from the original artifact, wanted to preserve
        if filter_type == 'water':
            query = {
                "breed": {"$in": ["Labrador Retriever Mix", "Chesapeake Bay Retriever", "Newfoundland"]},
                "sex_upon_outcome": "Intact Female",
                "age_upon_outcome_in_weeks": {"$gte": 26, "$lte": 156}
            }
        elif filter_type == 'mountain':
            query = {
                "breed": {"$in": ["German Shepherd", "Alaskan Malamute", "Old English Sheepdog", "Siberian Husky",
                                  "Rottweiler"]},
                "sex_upon_outcome": "Intact Male",
                "age_upon_outcome_in_weeks": {"$gte": 26, "$lte": 156}
            }
        elif filter_type == 'disaster':
            query = {
                "breed": {
                    "$in": ["Doberman Pinscher", "German Shepherd", "Golden Retriever", "Bloodhound", "Rottweiler"]},
                "sex_upon_outcome": "Intact Male",
                "age_upon_outcome_in_weeks": {"$gte": 20, "$lte": 300}
            }

        # Filters based on dropdown selection
        # only works if "Reset" is selected for buttons.
        else:
            if selected_type:
                query["animal_type"] = selected_type
            if selected_breed:
                query["breed"] = selected_breed

        # Fetch Data
        results = pd.DataFrame.from_records(db.read(query))

        if results.empty:
            return [], []

        # Removes MongoDB internal ID
        if "_id" in results.columns:
            results.drop(columns=["_id"], inplace=True)

        # Format age to be cleaner in table
        if "age_upon_outcome_in_weeks" in results.columns:
            results["age_upon_outcome_in_weeks"] = results["age_upon_outcome_in_weeks"].astype(float).round(1)

        # Formats date of birth to be cleaner in table
        date_cols = ["date_of_birth", "datetime"]
        for col in date_cols:
            if col in results.columns:
                results[col] = pd.to_datetime(results[col]).dt.strftime("%Y-%m-%d")

        # Only show needed columns in table and order them
        table_order = [
            "animal_id",
            "name",
            "animal_type",
            "breed",
            "age_upon_outcome_in_weeks",
            "sex_upon_outcome",
            "color",
            "date_of_birth",
            "outcome_subtype",
            "outcome_type"
        ]

        # Cleans up column names
        nice_names = {
            "animal_id": "ID",
            "name": "Name",
            "animal_type": "Type",
            "breed": "Breed",
            "date_of_birth": "DOB",
            "age_upon_outcome_in_weeks": "Age (Weeks)",
            "sex_upon_outcome": "Sex",
            "color": "Color",
            "outcome_subtype": "SubType",
            "outcome_type": "Outcome"
        }

        # Generate columns for fields
        columns = []
        for col_id in table_order:
            if col_id in results.columns:
                columns.append({"name": nice_names[col_id], "id": col_id})

        return results.to_dict("records"), columns

    # Pie Chart to show distribution of breeds
    @app.callback(
        Output("graph-id", "children"),
        Input("datatable-id", "derived_virtual_data")
    )
    def update_graph(view_data):
        if not view_data:
            return []

        df = pd.DataFrame(view_data)

        if "breed" not in df.columns:
            return []

        counts = df["breed"].value_counts()
        total = counts.sum()

        # Groups smaller breeds into "Other" to reduce clutter
        threshold = total * 0.01
        grouped = counts.copy()

        small = counts[counts < threshold].sum()
        grouped = grouped[counts >= threshold]

        if small > 0:
            grouped["Other"] = small

        pie_df = grouped.reset_index()
        pie_df.columns = ["breed", "count"]

        # Create pie chart
        fig = px.pie(
            pie_df,
            names="breed",
            values="count",
            title="Breed Distribution",
            width=600,
            height=600
        )
        fig.update_traces(textinfo="percent+label")

        # Ensure legend doesn't overlap
        fig.update_layout(legend=dict(y=0.5))

        return [dcc.Graph(figure=fig)]

    # Map to show location of selected animal
    @app.callback(
        Output("map-id", "children"),
        Input("datatable-id", "derived_virtual_data"),
        Input("datatable-id", "derived_virtual_selected_rows")
    )
    # Don't render map if no data is selected
    def update_map(view_data, selected_rows):
        if not view_data or not selected_rows:
            return []

        df = pd.DataFrame(view_data)

        # Validate index
        if selected_rows[0] >= len(df):
            return []

        i = selected_rows[0]

        # Check coordinates
        if "location_lat" not in df.columns or "location_long" not in df.columns:
            return []

        # Get the coordinates from the selected animal
        lat = df.iloc[i]["location_lat"]
        lon = df.iloc[i]["location_long"]
        name = df.iloc[i].get("name", "Unknown")
        breed = df.iloc[i].get("breed", "Unknown")

        # Render map and place marker on animals location
        return [
            dl.Map(
                center=[lat, lon],
                zoom=10,
                style={"width": "100%", "height": "400px"},
                children=[
                    dl.TileLayer(),
                    dl.Marker(
                        position=[lat, lon],
                        children=[
                            dl.Tooltip(breed),
                            dl.Popup([
                                html.H4(f"Name: {name}"),
                                html.P(f"Breed: {breed}")
                            ])
                        ]
                    )
                ]
            )
        ]