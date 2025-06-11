# load_env.ps1

# Defines the path to the .env file.
# $PSScriptRoot is the directory where this PowerShell script is located.
$envFilePath = Join-Path -Path $PSScriptRoot -ChildPath ".env"

Write-Host "Attempting to load variables from: $envFilePath" -ForegroundColor Cyan

# Check if the .env file exists.
if (-not (Test-Path $envFilePath)) {
    Write-Host "Error: .env file not found at the specified path: $envFilePath" -ForegroundColor Red
    Write-Host "Please ensure your .env file is in the same root directory as this script." -ForegroundColor Yellow
    exit 1 # Exit with an error code
}

# Read each line from the .env file.
Get-Content $envFilePath | ForEach-Object {
    $line = $_.Trim()

    # Ignore empty lines or lines starting with # (comments).
    if (-not ([string]::IsNullOrEmpty($line)) -and -not $line.StartsWith("#")) {
        # Split the line at the first '=' sign.
        $parts = $line.Split('=', 2)

        if ($parts.Count -eq 2) {
            $key = $parts[0].Trim()
            $value = $parts[1].Trim()

            # Set the environment variable.
            # Environment variables in PowerShell are set using $env:variable_name.
            Set-Item -Path Env:$key -Value $value
            Write-Host "Variable set: $key" -ForegroundColor Green
        } else {
            Write-Host "Warning: Line '$line' is not in key=value format and will be ignored." -ForegroundColor Yellow
        }
    }
}

Write-Host "Variables from .env have been loaded into the current PowerShell session." -ForegroundColor Green
Write-Host "You can now run your Spring Boot application." -ForegroundColor Green
Write-Host "Example: mvn spring-boot:run" -ForegroundColor Cyan