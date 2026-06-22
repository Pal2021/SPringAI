import os
from dotenv import load_dotenv
from azure.identity import DefaultAzureCredential
from azure.ai.projects import AIProjectClient

load_dotenv()

client = AIProjectClient(
    endpoint=os.getenv("AZURE_PROJECT_ENDPOINT"),
    credential=DefaultAzureCredential()
)

print("Beta.agents methods:", [m for m in dir(client.beta.agents) if not m.startswith("_")])