# Website Status Checker CLI

This command-line tool allows you to monitor the status of multiple websites simultaneously. It checks the availability of specified websites and provides status updates, such as whether they are up or down, along with response times.

## Features:
- **Fetch Command:** Fetches the current status of websites and saves them to a data store.
- **Live Command:** Starts live monitoring of websites, providing real-time status updates.
- **History Command:** Retrieves historical status information for specified websites.
- **Backup Command:** Creates a backup of website status data.
- **Restore Command:** Restores website status data from a backup file.

## Installation:
To install the tool, simply download the executable file for your platform from the releases section or compile from source.

## Usage:
- **Fetch Command:** `statuscheck fetch [url1 url2 ...]`
    - Fetches the status of specified websites or all configured websites if no URLs are provided.

- **Live Command:** `statuscheck live [url1 url2 ...]`
    - Starts live monitoring of specified websites or all configured websites if no URLs are provided.
    - Press Ctrl+C to stop monitoring.

- **History Command:** `statuscheck history [url1 url2 ...]`
    - Retrieves historical status information for specified websites or all configured websites if no URLs are provided.

- **Backup Command:** `statuscheck backup <backup_file_path>`
    - Creates a backup of website status data to the specified file path.

- **Restore Command:** `statuscheck restore <backup_file_path>`
    - Restores website status data from the specified backup file.

## License:
This project is licensed under the [MIT License](LICENSE).
