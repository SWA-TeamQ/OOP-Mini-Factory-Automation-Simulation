# 1. Create lib directory if it doesn't exist
if (!(Test-Path "lib")) {
    New-Item -ItemType Directory -Force -Path "lib"
}

# 2. Download SQLite JDBC Driver if missing
$sqliteJar = "lib/sqlite-jdbc-3.46.0.0.jar"
if (!(Test-Path $sqliteJar)) {
    Write-Host "Downloading SQLite JDBC Driver..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.46.0.0/sqlite-jdbc-3.46.0.0.jar" -OutFile $sqliteJar
}

# 3. Compile the project
Write-Host "Compiling project..." -ForegroundColor Cyan
if (!(Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" }
$sources = Get-ChildItem -Recurse -Filter *.java src/main/java | Select-Object -ExpandProperty FullName
javac -d bin -cp "$sqliteJar" $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit
}

# 4. Run the project
Write-Host "Starting Application..." -ForegroundColor Green
java -cp "bin;$sqliteJar" org.Automation.Main
