import os
import time
import dotenv
from dotenv import load_dotenv
from azure.identity import DefaultAzureCredential
from azure.ai.projects import AIProjectClient
from azure.ai.projects.models import PromptAgentDefinition, MCPTool

load_dotenv()

foundry_project_endpoint = os.getenv("AZURE_PROJECT_ENDPOINT")
model_deployment_name = os.getenv("MODEL_DEPLOYMENT_NAME")

project_client = AIProjectClient(
    endpoint=foundry_project_endpoint,
    credential=DefaultAzureCredential()
)

openai_client = project_client.get_openai_client()

# Get GitHub connection ID
github_connection_id = ""
for connection in project_client.connections.list():
    if connection.name == "GitHub":
        github_connection_id = connection.id
        break

print(f"GitHub Connection ID found: {github_connection_id}")

# Create GitHub MCP Tool
tool = MCPTool(
    server_label="github_server",
    server_url="https://api.githubcopilot.com/mcp/",
    require_approval="never",
    project_connection_id=github_connection_id
)

# Reuse agent if already created
AGENT_ID = os.getenv("MCP_AGENT_ID")

if not AGENT_ID:
    print("Creating new agent...")
    agent = project_client.agents.create_version(
        agent_name="GitHub-MCP-Agent",
        definition=PromptAgentDefinition(
            model=model_deployment_name,
            instructions="""You are a helpful GitHub assistant.
            You can search repositories, read code files, and find issues.
            Give clear and concise answers with repo names and links.""",
            tools=[tool],
        )
    )
    dotenv.set_key(".env", "MCP_AGENT_ID", agent.id)
    AGENT_ID = agent.id
    print(f"New agent created: {AGENT_ID}")
else:
    print(f"Reusing existing agent: {AGENT_ID}")

# Create conversation
conversation = openai_client.conversations.create()
print(f"Conversation created: {conversation.id}")

# Your question to GitHub
user_query = "Search for top Python FastAPI repositories with most stars"
print(f"\nAsking: {user_query}")

# Call with retry
for attempt in range(3):
    try:
        print(f"Attempt {attempt + 1}...")

        response = openai_client.responses.create(
            conversation=conversation.id,
            extra_body={
                "agent": {
                    "name": "GitHub-MCP-Agent",
                    "type": "agent_reference"
                }
            },
            input=user_query,
            timeout=120
        )

        print(f"\nResponse:\n{response.output_text}")
        break

    except Exception as e:
        error_msg = str(e)
        print(f"Attempt {attempt + 1} failed: {error_msg}")

        if any(k in error_msg.lower() for k in ["timed out", "timeout", "408", "time out"]):
            if attempt < 2:
                wait = (attempt + 1) * 15
                print(f"Timeout. Waiting {wait} seconds...")
                time.sleep(wait)
            else:
                print("All attempts timed out.")
        else:
            print(f"Non timeout error: {error_msg}")
            break