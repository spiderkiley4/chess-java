#!/bin/bash

# Build frontend
echo "Building frontend..."
cd frontend
npm install
npm run build
cd ..

# Ensure backend static directory exists
mkdir -p backend/src/main/resources/static

# Copy frontend build to backend
echo "Copying frontend build to backend..."
cp -r frontend/dist/* backend/src/main/resources/static/

# Build backend
echo "Building backend..."
cd backend
./gradlew clean bootJar
cd ..

# Copy the final JAR to the root directory
cp backend/build/libs/*.jar chess-game.jar

echo "Build complete! You can run the application with: java -jar chess-game.jar" 