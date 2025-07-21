#!/bin/bash

# Azure Infrastructure Deployment Script
# This script deploys the ARM template for Azure infrastructure resources

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Check if required parameters are provided
if [ $# -lt 3 ]; then
    print_message $RED "Usage: $0 <resource-group-name> <admin-object-id> [environment-name] [project-name] [location]"
    print_message $YELLOW "Example: $0 myapp-rg 12345678-1234-1234-1234-123456789012 dev myapp 'East US'"
    print_message $YELLOW "You can get your object ID with: az ad signed-in-user show --query id -o tsv"
    exit 1
fi

# Parameters
RESOURCE_GROUP_NAME=$1
ADMIN_OBJECT_ID=$2
ENVIRONMENT_NAME=${3:-"dev"}
PROJECT_NAME=${4:-"myapp"}
LOCATION=${5:-"East US"}

# Derived values
DEPLOYMENT_NAME="${PROJECT_NAME}-${ENVIRONMENT_NAME}-$(date +%Y%m%d-%H%M%S)"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEMPLATE_FILE="${SCRIPT_DIR}/azure-infrastructure.json"
PARAMETERS_FILE="${SCRIPT_DIR}/azure-infrastructure.parameters.json"

print_message $BLUE "Starting Azure infrastructure deployment..."
print_message $BLUE "Resource Group: $RESOURCE_GROUP_NAME"
print_message $BLUE "Environment: $ENVIRONMENT_NAME"
print_message $BLUE "Project: $PROJECT_NAME"
print_message $BLUE "Location: $LOCATION"
print_message $BLUE "Admin Object ID: $ADMIN_OBJECT_ID"
print_message $BLUE "Deployment Name: $DEPLOYMENT_NAME"

# Check if Azure CLI is installed
if ! command -v az &> /dev/null; then
    print_message $RED "Azure CLI is not installed. Please install it first."
    exit 1
fi

# Check if user is logged in
if ! az account show &> /dev/null; then
    print_message $RED "Please log in to Azure CLI first: az login"
    exit 1
fi

# Create resource group if it doesn't exist
print_message $YELLOW "Checking if resource group exists..."
if ! az group show --name "$RESOURCE_GROUP_NAME" &> /dev/null; then
    print_message $YELLOW "Creating resource group: $RESOURCE_GROUP_NAME"
    az group create --name "$RESOURCE_GROUP_NAME" --location "$LOCATION"
    print_message $GREEN "Resource group created successfully"
else
    print_message $GREEN "Resource group already exists"
fi

# Validate the ARM template
print_message $YELLOW "Validating ARM template..."
az deployment group validate \
    --resource-group "$RESOURCE_GROUP_NAME" \
    --template-file "$TEMPLATE_FILE" \
    --parameters location="$LOCATION" \
                environmentName="$ENVIRONMENT_NAME" \
                projectName="$PROJECT_NAME" \
                adminObjectId="$ADMIN_OBJECT_ID"

if [ $? -eq 0 ]; then
    print_message $GREEN "Template validation passed"
else
    print_message $RED "Template validation failed"
    exit 1
fi

# Deploy the ARM template
print_message $YELLOW "Deploying infrastructure..."
az deployment group create \
    --resource-group "$RESOURCE_GROUP_NAME" \
    --name "$DEPLOYMENT_NAME" \
    --template-file "$TEMPLATE_FILE" \
    --parameters location="$LOCATION" \
                environmentName="$ENVIRONMENT_NAME" \
                projectName="$PROJECT_NAME" \
                adminObjectId="$ADMIN_OBJECT_ID" \
    --verbose

if [ $? -eq 0 ]; then
    print_message $GREEN "Deployment completed successfully!"
    
    # Show deployment outputs
    print_message $BLUE "Deployment outputs:"
    az deployment group show \
        --resource-group "$RESOURCE_GROUP_NAME" \
        --name "$DEPLOYMENT_NAME" \
        --query properties.outputs \
        --output table
else
    print_message $RED "Deployment failed"
    exit 1
fi

print_message $GREEN "Infrastructure deployment complete!"
print_message $YELLOW "Remember to update the adminObjectId in the parameters file with your actual Object ID"
print_message $YELLOW "You can get your Object ID with: az ad signed-in-user show --query id -o tsv"