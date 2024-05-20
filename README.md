# Website Status Checker CLI

This command-line tool allows you to monitor the status of multiple websites simultaneously. It checks the availability of specified websites and provides status updates, such as whether they are up or down, along with response times.

## Features:
- **Fetch Command:** Fetches the current status of websites and saves them to a data store.
- **Live Command:** Starts live monitoring of websites, providing real-time status updates.
- **History Command:** Retrieves historical status information for specified websites.
- **Backup Command:** Creates a backup of website status data.
- **Restore Command:** Restores website status data from a backup file.

## Installation:
You can run the tool in an isolated Docker environment by following these steps:
1. Make sure you have docker installed in your machine. Unzip the file and go to project directory `StatusChecker`.
2. Now Build the Docker image: `docker build -t status-checker .` (incase of error re-try)
3. Access terminal of the container: `docker run -it status-checker`. Now you can run the cli using `sc`.

- Not recommended: You can also install java-17 and gradle/gradlew(windows), then run `gradle test`/`gradle build`, go to build/libs and you can access the cli using `java -jar <.jar filename> fetch ...` 

## Usage:
### Fetch Command

The `fetch` command is used to fetch the status of websites and save it to a data store. It supports the following options:

- `sc fetch` - Fetches the status of all configured websites.
- `sc fetch https://www.example.com` - Fetches the status of a specific website.
- `sc fetch --subset=4` - Fetches the status of a subset of configured websites (e.g., the first 4 websites).
- `sc fetch --show-result` - Fetches the status of all configured websites and displays the result in the console.
- `sc fetch --subset=5 --show-result` - Fetches the status of a subset of configured websites (e.g., the first 5 websites) and displays the result in the console.
- N.B: You can combine multiple arguments. Use `ls` comand to verify there is `data_store.json` where fetched data stored by default.

### Live Command

- `sc live` - Starts live monitoring of all configured websites in config json file.
- `sc live https://www.example.com` - Starts live monitoring of a specific website.
- `sc live --subset=3` - will check live status of only first 3 urls from config json file.
- `sc live --show-result` - Starts live monitoring of all configured websites and displays the result in the console.
- `sc live --interval=50 --show-result` - pull/check status with 50 seconds interval
- - N.B: You can combine multiple arguments. Press Ctrl+C to stop monitoring.

### History Command

- `sc history` - Retrieves historical status information for all configured websites.
- `sc history https://www.example.com` - Retrieves historical status information for a specific website.
- `sc history --page=2` - Shows only history from page 2 of datastore.

### Backup Command

- `sc backup <backup_file_path>` - Creates a backup of website status data to the specified file path.
- example: `sc backup bkp_data_20240520.json`

### Restore Command

- `sc restore <backup_file_path>` - Restores website status data from the specified backup file.
- example: `sc restore bkp_data_20240520.json`
- N.B: It won't replace previous data of data store.

## License:
This project is licensed under the [MIT License](LICENSE).
