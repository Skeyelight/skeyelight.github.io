from dash import Input, Output, State, no_update, ctx

def register_admin_callbacks(app, db):

    # Add Animal Callback
    @app.callback(
        Output("msg-add", "children"),
        Output("msg-add", "style"),
        Input("btn-add", "n_clicks"),
        # Basic
        State("new-type", "value"),
        State("new-name", "value"),
        State("new-breed", "value"),
        State("new-color", "value"),
        State("new-sex", "value"),
        State("new-dob", "value"),
        # Outcome & Age
        State("new-outcome-type", "value"),
        State("new-outcome-subtype", "value"),
        State("new-datetime", "value"),
        State("new-age-weeks", "value"),
        State("new-age-group", "value"),
        # Location
        State("new-lat", "value"),
        State("new-long", "value"),
        prevent_initial_call=True
    )
    def add_animal(n_clicks, animal_type, name, breed, color, sex, dob,
                   out_type, out_subtype, dt, age_weeks, age_group, lat, lon):

        if not n_clicks:
            return no_update, no_update

        # Ensure animal type is chosen
        if not animal_type:
            return "Error: Animal Type is required.", {"color": "red"}

        # Auto-generate ID
        try:
            last_entry = db.collection.find_one({}, sort=[("animal_id", -1)])
            if last_entry and "animal_id" in last_entry:
                last_id_str = last_entry["animal_id"]
                # Extract number
                last_num = int(last_id_str.replace("A", ""))
                new_id = f"A{last_num + 1}"
            else:
                new_id = "A000001"
        except (ValueError, TypeError, IndexError):
            # Fallback if ID format is unexpected
            new_id = "A999999"

        # Construct Document
        data = {
            "animal_id": new_id,
            "animal_type": animal_type,
            "name": name,
            "breed": breed,
            "color": color,
            "sex_upon_outcome": sex,
            "date_of_birth": dob,
            "outcome_type": out_type,
            "outcome_subtype": out_subtype,
            "datetime": dt,
            "month_year": dt,
            "age_upon_outcome_in_weeks": age_weeks,
            "age_upon_outcome": age_group,
            "location_lat": lat,
            "location_long": lon
        }

        # Clean up None values
        data = {k: v for k, v in data.items() if v is not None and v != ""}

        if db.create(data):
            return f"Successfully added {name or 'Animal'} (ID: {new_id})", {"color": "green"}
        else:
            return "Error: Could not insert record.", {"color": "red"}

    # Update / Delete Callback
    @app.callback(
        Output("msg-manage", "children"),
        Output("msg-manage", "style"),
        Input("btn-update", "n_clicks"),
        Input("btn-delete", "n_clicks"),
        State("target-id", "value"),
        # Basic
        State("up-type", "value"),
        State("up-name", "value"),
        State("up-breed", "value"),
        State("up-color", "value"),
        State("up-sex", "value"),
        State("up-dob", "value"),
        # Outcome
        State("up-outcome-type", "value"),
        State("up-outcome-subtype", "value"),
        State("up-datetime", "value"),
        State("up-age-weeks", "value"),
        State("up-age-group", "value"),
        # Location
        State("up-lat", "value"),
        State("up-long", "value"),
        prevent_initial_call=True
    )
    def manage_animal(_btn_up, _btn_del, target_id, animal_type, name, breed, color, sex, dob,
                      out_type, out_subtype, dt, age_weeks, age_group, lat, lon):

        trigger = ctx.triggered_id
        if not target_id:
            return "Please enter a Target Animal ID.", {"color": "orange"}

        # Delete entry
        if trigger == "btn-delete":
            count = db.delete({"animal_id": target_id})
            if count > 0:
                return f"Deleted {count} record(s) with ID {target_id}", {"color": "green"}
            else:
                return f"No record found with ID {target_id}", {"color": "orange"}

        # Update entry
        if trigger == "btn-update":
            update_data = {}

            # Only add fields that have values
            if animal_type: update_data["animal_type"] = animal_type
            if name: update_data["name"] = name
            if breed: update_data["breed"] = breed
            if color: update_data["color"] = color
            if sex: update_data["sex_upon_outcome"] = sex
            if dob: update_data["date_of_birth"] = dob

            if out_type: update_data["outcome_type"] = out_type
            if out_subtype: update_data["outcome_subtype"] = out_subtype
            if dt:
                update_data["datetime"] = dt
                update_data["month_year"] = dt
            if age_weeks: update_data["age_upon_outcome_in_weeks"] = age_weeks
            if age_group: update_data["age_upon_outcome"] = age_group

            if lat: update_data["location_lat"] = lat
            if lon: update_data["location_long"] = lon

            if not update_data:
                return "No new data entered to update.", {"color": "orange"}

            count = db.update({"animal_id": target_id}, update_data)

            if count > 0:
                return f"Updated {count} record(s).", {"color": "green"}
            else:
                return f"No record found to update with ID {target_id}", {"color": "orange"}

        return no_update, no_update