# 1. Create lib directory if it doesn't exist
if (!(Test-Path "lib")) {
    New-Item -ItemType Directory -Force -Path "lib"
}

# 2. Download Dependencies if missing
$sqliteJar = "lib/sqlite-jdbc-3.46.0.0.jar"
$slf4jApi = "lib/slf4j-api-1.7.36.jar"
$slf4jSimple = "lib/slf4j-simple-1.7.36.jar"

$dependencies = @(
    @{ name = "SQLite JDBC"; url = "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.46.0.0/sqlite-jdbc-3.46.0.0.jar"; file = $sqliteJar },
    @{ name = "SLF4J API"; url = "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"; file = $slf4jApi },
    @{ name = "SLF4J Simple"; url = "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar"; file = $slf4jSimple }
)

foreach ($dep in $dependencies) {
    if (!(Test-Path $dep.file)) {
        Write-Host "Downloading $($dep.name)..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $dep.url -OutFile $dep.file
    }
}

$classpath = "bin;$sqliteJar;$slf4jApi;$slf4jSimple"

# 3. Compile the project
Write-Host "Compiling project..." -ForegroundColor Cyan
if (!(Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" }
$sources = Get-ChildItem -Recurse -Filter *.java src/main/java | Select-Object -ExpandProperty FullName
javac -d bin -cp "$classpath" $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit
}

# 4. Run the project
Write-Host "Starting Application..." -ForegroundColor Green
java -cp "$classpath" org.Automation.Main $args
