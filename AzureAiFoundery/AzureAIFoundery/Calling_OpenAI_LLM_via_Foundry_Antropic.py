import os
from dotenv import load_dotenv
import anthropic

load_dotenv()  # reads your .env file

client = anthropic.AnthropicFoundry(
    base_url=os.getenv("AZURE_FOUNDRY_URL"),
    api_key=os.getenv("AZURE_API_KEY")
)

message = client.messages.create(
    model="claude-sonnet-4-6",
    max_tokens=1024,
    messages=[
        {"role": "user", "content": "Hello!"}
    ]
)

print(message.content[0].text)