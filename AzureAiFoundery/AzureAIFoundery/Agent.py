import os
import subprocess
import tempfile
from dotenv import load_dotenv
import anthropic

load_dotenv()

client = anthropic.AnthropicFoundry(
    base_url=os.getenv("AZURE_FOUNDRY_URL"),
    api_key=os.getenv("AZURE_API_KEY")
)

conversation = []

def ask_claude(user_message):
    conversation.append({
        "role": "user",
        "content": user_message
    })

    response = client.messages.create(
        model="claude-sonnet-4-6",
        max_tokens=2048,
        system="""You are a helpful coding assistant. 
When you need to calculate something, write Python code inside <code> tags like this:
<code>
print(2 + 2)
</code>
I will run the code and give you the result.""",
        messages=conversation
    )

    reply = response.content[0].text
    conversation.append({
        "role": "assistant",
        "content": reply
    })

    return reply

def run_code(code):
    with tempfile.NamedTemporaryFile(mode='w', suffix='.py', delete=False) as f:
        f.write(code)
        f.flush()
        result = subprocess.run(
            ["python", f.name],
            capture_output=True,
            text=True,
            timeout=10
        )
        return result.stdout or result.stderr

def extract_code(text):
    if "<code>" in text and "</code>" in text:
        start = text.index("<code>") + 6
        end = text.index("</code>")
        return text[start:end].strip()
    return None

def agent_loop(task):
    print(f"\nTask: {task}")
    print("-" * 40)

    # First call
    reply = ask_claude(task)
    print(f"Claude: {reply}")

    # Check if Claude wrote code
    code = extract_code(reply)
    if code:
        print(f"\nRunning code...")
        result = run_code(code)
        print(f"Code output: {result}")

        # Send result back to Claude
        final_reply = ask_claude(f"Code output was: {result}\nNow give me the final answer.")
        print(f"\nFinal Answer: {final_reply}")
    
    return reply

# Run it
agent_loop("Calculate compound interest on 10000 rupees at 8% for 5 years, show year by year breakdown")